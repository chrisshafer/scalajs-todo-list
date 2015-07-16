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

  val toDoList = scala.collection.mutable.Map[Int,String]()

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

  case class ToDoListProps(items:scala.collection.mutable.Map[Int,String], remove: (Int) => Unit )
  val TodoList = ReactComponentB[ToDoListProps]("TodoList")
    .render(props => {
    <.div(
      ^.className := "table-responsive",
      <.table(
        ^.className := "table table-striped",
        <.thead(<.tr(<.th("Task"),<.th("remove"))),
        <.tbody(
          props.items.map{ case (i , x) => ListItem(ItemProps(i,x,props.remove))}
        )
      )
    )
  }).build

  case class State(items: scala.collection.mutable.Map[Int,String], text: String)
  class ToDoBackend($: BackendScope[Unit, State]) {
    def onChange(e: ReactEventI) =
      $.modState(_.copy(text = e.target.value))
    def addToDo(e: ReactEventI) = {
      e.preventDefault()
      $.modState(s => State(s.items += s.items.size + 1 -> s.text  , ""))
    }
    def handleRemove(id: Int) = {
      $.modState(s => State(s.items -= id  , s.text))
    }
  }

  val TodoApp = ReactComponentB[Unit]("TodoApp")
    .initialState(State(toDoList, ""))
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
