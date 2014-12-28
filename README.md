# cljs-web-audio
A very basic interface for Web Audio.

This project is written in [clojurescript](https://github.com/clojure/clojurescript), uses [om] (https://github.com/swannodette/om), and is developed using [chestnut](https://github.com/plexus/chestnut).

(Note: Most of the text for this REAMDE is part of the [chestnut lein
template](https://github.com/plexus/chestnut). I just edited it a bit.)


## Trying it out

Start a REPL (in a terminal: `lein repl`, or from Emacs: open a
clj/cljs file in the project, then do `M-x cider-jack-in`. Make sure
CIDER is up to date).

In the REPL do

```clojure
(run)
(browser-repl)
```

When you see the line `Successfully compiled "resources/public/app.js"
in 21.36 seconds.`, you're ready to go. Browse to
`http://localhost:10555` and enjoy.


## Using the repl (with chestnut)

The call to `(run)` does two things, it starts the webserver at port
10555, and also the Figwheel server which takes care of live reloading
ClojureScript code and CSS. Give them some time to start.

Running `(browser-repl)` starts the Weasel REPL server, and drops you
into a ClojureScript REPL. Evaluating expressions here will only work
once you've loaded the page, so the browser can connect to Weasel.

**Attention: It is not longer needed to run `lein figwheel`
  separately. This is now taken care of behind the scenes**


## License

Copyright © 2014 Eliana Medina

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
