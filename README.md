
# ctc-departure-items-frontend

This service allows a user to complete the items section of a transit movement departure.

Service manager port: 10127

### Testing

Run unit tests:
<pre>sbt test</pre>  
Run integration tests:
<pre>sbt it/test</pre>
Run accessibility linter tests:
<pre>sbt A11y/test</pre>

### Running manually or for journey tests

To toggle between the Phase 5 transition and post-transition modes we have defined two separate modules, each with their own set of class bindings to handle the rules associated with these two periods.

#### Transition
<pre>
sm2 --start CTC_TRADERS_P5_ACCEPTANCE_TRANSITION
sm2 --stop CTC_DEPARTURE_ITEMS_FRONTEND_TRANSITION
sbt -Dplay.additional.module=config.TransitionModule run
</pre>

#### Final
<pre>
sm2 --start CTC_TRADERS_P5_ACCEPTANCE
sm2 --stop CTC_DEPARTURE_ITEMS_FRONTEND
sbt -Dplay.additional.module=config.PostTransitionModule run
</pre>

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").