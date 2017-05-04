import play.api.data.validation.{Constraint, Invalid, Valid}

package object controllers {
    implicit class MultiConstraints[T](cons: Constraint[T]) {
        def and(other: Constraint[T]): Constraint[T] = Constraint { field: T =>
            cons(field) match {
                case Valid => other(field)
                case fail@Invalid(_) => fail
            }
        }

        def or(other: Constraint[T]): Constraint[T] = Constraint { field: T =>
            cons(field) match {
                case valid@Valid => valid
                case Invalid(_) => other(field)
            }
        }
    }
}
