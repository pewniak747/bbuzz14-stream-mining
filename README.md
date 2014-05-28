bbuzz14-stream-mining
=====================

A Scala starter pack for the [stream mining hackday](http://lanyrd.com/2014/data-stream-mining-hackathon/scymzx/) at [BBUZZ 2014](http://berlinbuzzwords.de/hackathons-meetups).

This repository should help you getting started by providing the boilerplate of how to obtains tweets.

## See also ##

- Clojure starter: https://github.com/sojoner/stromer
- Python starter: https://github.com/truemped/bbhack-2014
- Scala starter (this repository): https://github.com/knutwalker/bbuzz14-stream-mining


## Requirements ##

- Java 1.6 or later
- If you have a bash or a similar shell at hand, you can use the provided `sbt` starter,
  otherwise, you have to install [sbt](http://www.scala-sbt.org/)
- (git)

## Usage ##

### checkout

```bash
git clone https://github.com/knutwalker/bbuzz14-stream-mining.git && cd bbuzz14-stream-mining
```

## Use at bbuzz hackday 2014-05-28

```bash
./sbt run
```


_otherwise_


### compile

```bash
./sbt compile
```

### run an example

```bash
./sbt run
```

### open the REPL

```bash
./sbt console
```


### using the sbt shell

```bash
./sbt
```

Use `re-start` to start an example and `re-stop` to stop an example.


## Start hacking ##

Have a look at the provided [examples](src/bbuzz/example/example.scala).
You have to specify two things:

**1**. How you want to deal with Tweets.

Implement your own [TweetConsumer](src/bbuzz/example/example.scala#L31-52).

**2**. Choose a [TweetProvider](src/bbuzz/example/example.scala#L70-76)

By mixing in a concrete TweetProvider and filling in the required parameters (such as `host`),
you create a _mainable_ object, that you can run.

If you're unsure about what parameters are required — the compiler will tell you when something's missing.

### Scaladoc

The scaladoc is available at [this github page](http://knutwalker.github.io/bbuzz14-stream-mining/scaladoc/).


### License

This project, [twitter4j](http://twitter4j.org/en/index.html), [RxJava](https://github.com/Netflix/RxJava), and [Json4s](http://json4s.org/) are available under [Apache Software License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

[Dispatch](http://dispatch.databinder.net/Dispatch.html) and [JeroMQ](https://github.com/zeromq/jeromq) are distributed unter [LGPLv3 License](http://opensource.org/licenses/lgpl-3.0.html).

[Jedis](https://github.com/xetorthio/jedis) is available under [MIT License](https://raw.githubusercontent.com/xetorthio/jedis/master/LICENSE.txt).

The [sbt starter](https://github.com/paulp/sbt-extras) is available under [BSD License](https://raw.githubusercontent.com/paulp/sbt-extras/master/LICENSE.txt)
