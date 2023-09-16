package meals

import meals.application.MealsComponents
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import play.api.BuiltInComponents

trait MealsPlaySpec extends PlaySpec with OneAppPerSuiteWithComponents {

  lazy val mealsComponents: MealsComponents = new MealsComponents(context)

  override lazy val components: BuiltInComponents = mealsComponents

}
