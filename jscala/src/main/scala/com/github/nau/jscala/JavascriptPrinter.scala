package com.github.nau.jscala

import com.github.nau._

object JavascriptPrinter {
  def print(ast: JsAst, indent: Int): String = {
    def p(ast: JsAst) = print(ast, indent)
    def p2(ast: JsAst) = " " * (indent + 2) + print(ast, indent + 2)
    def s(ast: JsAst) = ast match {
      case _: JsLit => p(ast)
      case _: JsIdent => p(ast)
      case _: JsCall => p(ast)
      case s => s"(${p(ast)})"
    }
    ast match {
      case JsBool(value)                        => value.toString
      case JsString(value)                      => "\"" + value + "\""
      case JsNum(value, true)                   => value.toString
      case JsNum(value, false)                  => value.toLong.toString
      case JsIdent(value)                       => value
      case JsSelect(qual, "apply")              => s"${p(qual)}"
      case JsSelect(qual, name)                 => s"${p(qual)}.$name"
      case JsUnOp(operator, operand)            => s"${operator}${s(operand)}"
      case JsBinOp("=", lhs, rhs)               => s"${s(lhs)} = ${s(rhs)}"
      case JsBinOp(operator, lhs, rhs)          => s"(${s(lhs)}) $operator ${s(rhs)}"
      case JsCall(callee, params)               => s"""${p(callee)}(${params.map(p(_)).mkString(", ")})"""
      case JsBlock(stmts)                       => s"""{\n${stmts.map(p2(_)).mkString(";\n")}\n${" " * indent}}"""
      case JsExprStmt(jsExpr)                   => p(jsExpr)
      case JsIf(cond, thenp, elsep)             => s"if (${p(cond)}) ${p(thenp)}" + elsep.map(e => s" else ${p(e)}").getOrElse("")
      case JsWhile(cond, body)                  => s"while (${p(cond)}) ${p(body)}"
      case JsVarDef(identifier, initializer)    => s"var ${identifier} = ${p(initializer)}"
      case JsFunDecl(identifier, params, body)  => s"""function ${identifier}(${params.mkString(", ")}) ${p(body)}"""
      case JsAnonFunDecl(params, body)          => s"""function (${params.mkString(", ")}) ${p(body)}"""
      case JsAnonObjDecl(fields)                => fields.map{ case (k, v) => s""" $k: ${p(v)}"""}.mkString("{", ",\n", "}")
      case JsReturn(jsExpr)                     => s"return ${p(jsExpr)};"
      case JsUnit                               => ""
    }
  }
}
