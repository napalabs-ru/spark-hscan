package ru.napalabs.spark.hscan.funcs

import org.apache.commons.lang3.StringEscapeUtils
import org.apache.spark.sql.catalyst.expressions.codegen.{CodegenContext, ExprCode}
import org.apache.spark.sql.catalyst.expressions.{BinaryExpression, Expression, ExpressionDescription, ImplicitCastInputTypes, NullIntolerant}
import org.apache.spark.sql.catalyst.util.GenericArrayData
import org.apache.spark.sql.types.{BooleanType, DataType, DataTypes, StringType}

/**
 * Hyperscan RegEx pattern matching function
 */
@ExpressionDescription(
  usage = "_FUNC_(str, patterns) - Returns true if str matches one of pattern, " +
    "null if any arguments are null, false otherwise.",
  arguments ="""
  * str - a string expression
  * patterns - a array of strings expression. Should always be foldable (eg array('pattern1', 'pattern2')).
  """
)
case class HyperscanLike(left: Expression, right: Expression) extends BinaryExpression
  with ImplicitCastInputTypes with NullIntolerant {

  override def dataType: DataType = BooleanType

  override def inputTypes: Seq[DataType] = Seq(StringType, DataTypes.createArrayType(StringType))


  override protected def doGenCode(ctx: CodegenContext, ev: ExprCode): ExprCode = {
    val exprClass = classOf[com.gliwka.hyperscan.wrapper.Expression].getName
    val dbClass = classOf[com.gliwka.hyperscan.wrapper.Database].getName
    val scannerClass = classOf[com.gliwka.hyperscan.wrapper.Scanner].getName
    val compileException = classOf[com.gliwka.hyperscan.wrapper.CompileErrorException].getName
    val runtimeException = classOf[java.lang.RuntimeException].getName

    if (right.foldable) {
      val rVal = right.eval()

      if (rVal != null) {
        val staticArr = rVal.asInstanceOf[GenericArrayData]
        var regexes = Seq[String]()
        for (i <- 0 until staticArr.numElements()) {
          regexes = regexes :+ ("\"" + StringEscapeUtils.escapeJava(staticArr.getUTF8String(i).toString) + "\"")
        }
        val tmpArrVar = "regexesHyperscanTmp"
        val arrayInitCode = s"String[] $tmpArrVar = {" + regexes.mkString(",") + "};"

        val exprVariable = ctx.addMutableState(s"""java.util.List<$exprClass>""", "exprHLike",
          v =>
            s"""
               $v = new java.util.ArrayList<$exprClass>();
               $arrayInitCode
               for(int i = 0; i < $tmpArrVar.length; i++) {
                $v.add(new $exprClass($tmpArrVar[i]));
               }
               """)
        val dbVariable = ctx.addMutableState(dbClass, "dbHLike",
          v => s"""try {$v = $dbClass.compile($exprVariable);} catch($compileException e) {throw new $runtimeException("cant compile pattern", e);}""")
        val scannerVariable = ctx.addMutableState(scannerClass, "scannerHLike",
          v =>
            s"""
              $v = new $scannerClass();
              $v.allocScratch($dbVariable);
            """)

        val eval = left.genCode(ctx)
        ev.copy(code =
          s"""
           ${eval.code}
           boolean ${ev.isNull} = ${eval.isNull};
           ${ctx.javaType(dataType)} ${ev.value} = ${ctx.defaultValue(dataType)};
           if (!${ev.isNull}) {
            ${ev.value} = !$scannerVariable.scan($dbVariable, ${eval.value}.toString()).isEmpty();
           }
           """)
      } else {
        ev.copy(code =
          s"""
            boolean ${ev.isNull} = true;
            ${ctx.javaType(dataType)} ${ev.value} = ${ctx.defaultValue(dataType)}
           """)
      }
    } else {
      throw new RuntimeException("Cannot evaluate right expression (should be foldable)")
    }
  }

  override def toString: String = s"$left HLIKE $right"
}
