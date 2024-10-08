#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.$package$.routes"

if [ ! -f ../conf/app.$package$.routes ]; then
  echo "Write into app.routes file"
  awk '
  /# microservice specific routes/ {
    print;
    print "";
    next;
  }
  /^\$/ {
    if (!printed) {
      printed = 1;
      print "->         /                                            app.$package$.Routes";
      next;
    }
    print;
    next;
  }
  {
    if (!printed) {
      printed = 1;
      print "->         /                                            app.$package$.Routes";
    }
    print
  }' ../conf/app.routes > tmp && mv tmp ../conf/app.routes
fi

echo "" >> ../conf/app.$package$.routes
echo "GET        /:lrn/$package;format="packaged"$/$title;format="normalize"$                        controllers.$package$.$className$Controller.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.$package$.routes
echo "POST       /:lrn/$package;format="packaged"$/$title;format="normalize"$                        controllers.$package$.$className$Controller.onSubmit(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.$package$.routes

echo "GET        /:lrn/$package;format="packaged"$/change-$title;format="normalize"$                 controllers.$package$.$className$Controller.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.$package$.routes
echo "POST       /:lrn/$package;format="packaged"$/change-$title;format="normalize"$                 controllers.$package$.$className$Controller.onSubmit(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.$package$.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.title = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.heading = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.checkYourAnswersLabel = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.hint = For example, 14 1 2020" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required.all = Enter the date for $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required.two = The date for $title$" must include {0} and {1} >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required = The date for $title$ must include {0}" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.invalid = Enter a real date for $title$" >> ../conf/messages.en

if grep -q "implicit class ErrorSummaryImplicits" ../app/views/utils/ViewUtils.scala; then
  echo "Implicit class 'ErrorSummaryImplicits' already exists in ViewUtils. No changes made."
else
  awk '/object ViewUtils \{/{
      print;
      print "";
      print "    import uk.gov.hmrc.hmrcfrontend.views.implicits.RichErrorSummarySupport";
      print "    import play.api.data.Form";
      print "    import java.time.LocalDate";
      print "";
      print "  implicit class ErrorSummaryImplicits(errorSummary: ErrorSummary)(implicit messages: Messages) extends RichErrorSummarySupport {";
      print "";
      print "    private def withErrorMapping[T](form: Form[T], fieldName: String, args: Seq[String]): ErrorSummary = {";
      print "      val arg = form.errors.flatMap(_.args).find(args.contains).getOrElse(args.head).toString";
      print "      errorSummary.withFormErrorsAsText(form, mapping = Map(fieldName -> s\"\${fieldName}_\$arg\"))";
      print "    }";
      print "";
      print "    def withDateErrorMapping(form: Form[LocalDate], fieldName: String): ErrorSummary = {";
      print "      val args = Seq(\"day\", \"month\", \"year\")";
      print "      withErrorMapping(form, fieldName, args)";
      print "    }";
      print "  }";
      next;
  }
  { print }' ../app/views/utils/ViewUtils.scala > tmp && mv tmp ../app/views/utils/ViewUtils.scala
  echo "Implicit class 'ErrorSummaryImplicits' has been added to ViewUtils."
fi

echo "Migration $className;format="snake"$ completed"
