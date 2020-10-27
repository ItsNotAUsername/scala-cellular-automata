package model

trait Cell[T <: Cell[T]]

trait Rule[T <: Cell[T]] {
  def transform(cell: Cell[T], neighbours: scala.collection.mutable.Seq[Cell[T]]): Cell[T]
}