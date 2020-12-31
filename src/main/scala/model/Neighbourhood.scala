package model

trait Neighbourhood {

  def neighbours(x: Int, y: Int): Seq[(Int, Int)]

}

object Neighbourhood {

  object Moore extends Neighbourhood {

    override def neighbours(x: Int, y: Int): Seq[(Int, Int)] =
      Seq(
        (x - 1, y - 1), (x, y - 1), (x + 1, y - 1),
        (x - 1, y    ),             (x + 1, y    ),
        (x - 1, y + 1), (x, y + 1), (x + 1, y + 1)
      )

    override def toString: String = "Moore"

  }

  object VonNeumann extends Neighbourhood {

    override def neighbours(x: Int, y: Int): Seq[(Int, Int)] =
      Seq(
                    (x, y - 1),
        (x - 1, y),             (x + 1, y),
                    (x, y + 1)
      )

    override def toString: String = "Von Neumann"

  }

}
