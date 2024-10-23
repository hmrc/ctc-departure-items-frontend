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
echo "GET        /$package;format="packaged"$/$title;format="normalize"$/:lrn                        controllers.$package$.$className$Controller.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.$package$.routes
echo "POST       /$package;format="packaged"$/$title;format="normalize"$/:lrn                        controllers.$package$.$className$Controller.onSubmit(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.$package$.routes

echo "GET        /$package;format="packaged"$/change-$title;format="normalize"$/:lrn                 controllers.$package$.$className$Controller.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.$package$.routes
echo "POST       /$package;format="packaged"$/change-$title;format="normalize"$/:lrn                 controllers.$package$.$className$Controller.onSubmit(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.$package$.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.title = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.heading = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.checkYourAnswersLabel = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.hint = For example, 14 1 2020" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required.all = Enter the date for $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required.multiple = The date for $title$" must include {0} and {1} >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required = The date for $title$ must include {0}" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.invalid.all = The date for $title$ must only include numbers 0 to 9" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.invalid.multiple = The date {0} and {1} for $title$ must only include numbers 0 to 9" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.invalid = The date {0} for $title$ must only include numbers 0 to 9" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.min.date = The date must be after {0}" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.max.date = The date must be on or before the current date" >> ../conf/messages.en

if grep -q "implicit class ErrorSummaryImplicits" ../app/views/utils/ViewUtils.scala; then
  echo "Implicit class 'ErrorSummaryImplicits' already exists in ViewUtils. No changes made."
else
  awk '/object ViewUtils \{/{
      print;
      print "";
      print "  import uk.gov.hmrc.hmrcfrontend.views.implicits.RichErrorSummarySupport";
      print "  import play.api.data.Form";
      print "  import java.time.LocalDate";
      print "";
      print "  implicit class ErrorSummaryImplicits(errorSummary: ErrorSummary)(implicit messages: Messages) extends RichErrorSummarySupport {";
      print "";
      print "    private def withErrorMapping[T](form: Form[T], fieldName: String, args: Seq[String]): ErrorSummary = {";
      print "      val arg = form.errors.flatMap(_.args).find(args.contains).getOrElse(args.head).toString";
      print "      errorSummary.withFormErrorsAsText(form, mapping = Map(fieldName -> s\"\${fieldName}.\$arg\"))";
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

if grep -q "protected def localDate" ../app/forms/mappings/Mappings.scala; then
  echo "Function 'localDate' already exists in Mappings. No changes made."
else
  awk '/trait Mappings extends Formatters with Constraints \{/{
      print;
      print "";
      print "  import java.time.LocalDate";
      print "";
      print "  protected def localDate(";
      print "    invalidKey: String,";
      print "    requiredKey: String";
      print "  ): FieldMapping[LocalDate] =";
      print "    of(new LocalDateFormatter(invalidKey, requiredKey))";
      next;
  }
  { print }' ../app/forms/mappings/Mappings.scala > tmp && mv tmp ../app/forms/mappings/Mappings.scala
  echo "Function 'localDate' has been added to Mappings."
fi

if grep -q "implicit class DateTimeRichFormErrors" ../app/views/utils/ViewUtils.scala; then
  echo "implicit class 'DateTimeRichFormErrors' already exists in ViewUtils. No changes made."
else
  awk '/object ViewUtils \{/{
      print;
      print "";
      print "  implicit class DateTimeRichFormErrors(formErrors: Seq[FormError])(implicit messages: Messages) {";
      print "   import forms.mappings.LocalDateFormatter";
      print "   import uk.gov.hmrc.govukfrontend.views.html.components.implicits.*";
      print "   import uk.gov.hmrc.govukfrontend.views.viewmodels.errorsummary.ErrorLink";
      print "";
      print "    def toErrorLinks: Seq[ErrorLink] =";
      print "      formErrors.map {";
      print "        formError =>";
      print "          val args = LocalDateFormatter.fieldKeys";
      print "          val arg  = formError.args.find(args.contains).getOrElse(args.head).toString";
      print "          val key  = s\"#\${formError.key}.\$arg\"";
      print "          ErrorLink(href = Some(key), content = messages(formError.message, formError.args*).toText)";
      print "      }";
      print "  }";
      next;
  }
  { print }' ../app/views/utils/ViewUtils.scala > tmp && mv tmp ../app/views/utils/ViewUtils.scala
  echo "implicit class 'DateTimeRichFormErrors' has been added to ViewUtils."
fi

echo "Migration $className;format="snake"$ completed"
