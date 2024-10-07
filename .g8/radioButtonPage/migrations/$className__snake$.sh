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
echo "$package$.$className;format="decap"$.$option1key;format="decap"$ = $option1msg$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.$option2key;format="decap"$ = $option2msg$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.checkYourAnswersLabel = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required = Select  $title$" >> ../conf/messages.en

echo "Adding to ModelGenerators"
awk '/self: Generators =>/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrary$className$: Arbitrary[models.$package$.$className$] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(models.$package$.$className$.values)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Migration $className;format="snake"$ completed"
