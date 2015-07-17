import japgolly.scalajs.react._
import org.scalajs.dom.document
import org.scalajs.dom.raw.HTMLInputElement
import org.scalajs.jquery._

import scala.scalajs.js._
import scala.scalajs.js.JSApp
import japgolly.scalajs.react.vdom.prefix_<^._
/**
 * Created by chris on 7/12/15.
 */
object ToDoApp extends JSApp {


  case class ItemProps(key: Int, value: String, remove: (Int) => Unit)
  val ListItem = ReactComponentB[ItemProps]("ToDoItem")
    .render( props => {
    <.tr(
        <.td(<.span(props.value)),
        <.td(<.button(
          "-",
          ^.className := "btn btn-default",
          ^.onClick --> props.remove(props.key)
        ))
      )
  }).build

  case class ToDoListProps(items: Seq[String], remove: (Int) => Unit )
  val TodoList = ReactComponentB[ToDoListProps]("TodoList")
    .render(props => {
    <.div(
      ^.className := "table-responsive",
      <.table(
        ^.className := "table table-striped",
        <.thead(<.tr(<.th("Task"),<.th("remove"))),
        <.tbody(
          props.items.zipWithIndex.map{ case (i , x) => ListItem(ItemProps(x,i,props.remove))}
        )
      )
    )
  }).build

  case class State(items: Seq[String], text: String){
    def removeIndex[A]( list : Seq[A], index: Int): Seq[A] ={
      val (before,at) = list.splitAt(index)
      before ++ at.tail
    }
    def withoutItem(index: Int): State ={
      State(removeIndex(items,index),text)
    }

  }
  class ToDoBackend($: BackendScope[Unit, State]) {
    val toDoList = Map[Int,String]()
    def onChange(e: ReactEventI) =
      $.modState(_.copy(text = e.target.value))
    def addToDo(e: ReactEventI) = {
      e.preventDefault()
      $.modState{s : State => State(s.items :+ s.text  , "")}
    }
    def handleRemove(index: Int) = {
      $.modState{s : State => s.withoutItem(index)}
    }
  }

  val TodoApp = ReactComponentB[Unit]("TodoApp")
    .initialState(State(Seq(), ""))
    .backend(new ToDoBackend(_))
    .render((_,S,B) =>
    <.div(
      ^.className := "container-fluid",
      <.h3("TODO list"),
      <.form(
        ^.className := "form-inline",
        <.input(^.onChange ==> B.onChange, ^.value := S.text, ^.className := "form-control"),
        <.button(
          "Add todo",
          ^.className := "btn btn-default",
          ^.onClick ==> B.addToDo)
      ),
      TodoList(ToDoListProps(S.items,B.handleRemove))
    )
    ).buildU

  def main(): Unit ={
    React.render(TodoApp(), document.body)
  }
}
