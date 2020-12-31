package model

trait Rule[T <: Cell[T]] {

  def transform(cell: T, neighbours: Seq[T]): T

}