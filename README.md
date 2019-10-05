# Example re-frame/re-com app with AWS Amplify based Authenticator

A [re-frame](https://github.com/Day8/re-frame) application that demonstrates how
to use AWS Amplify Cognito with an automautomatic Authenticator wrapper. It uses
the AWS Amplify react UI features with almost no code on your part.

With a very simple wrapper around the app, you automatically get full
integration with AWS Cognito and a UI to do 
* _Sign-up_ 
* _Sign-in_ 
* _Sign-out_
* _Password change_

You don't have to write any UI or detailed AWS code to get all these features.
You can easily add other AWS Amplify services on top of this.

Once you have this basic setup, its easy to add Federated Social Login, OTP,
etc. just by following the AWS Amplify documentation.

## Build From Scratch

See [How to Build From Scratch](docs/how_to_build_from_scratch.md) for a full explanation on how to:
* Create initial re-frame / re-com / shadow-cljs project scaffolding
* Add AWS Amplify to the project
* Add the Amplify Authenticator to the project

### Prerequisites
* An AWS Account that you have administrative access to
* git
* Java
* Node.js and npm
* Clojure
* Clojurscript
* shadow-cljs
* leiningen
* Have an editor and know how to use it 

## Development Mode

### Start Cider from Emacs:

Refer to the [shadow-cljs Emacs / CIDER documentation](https://shadow-cljs.github.io/docs/UsersGuide.html#cider).

The mentioned `dir-local.el` file has been created.

### Compile css:

Compile css file once.

```
lein garden once
```

Automatically recompile css file on change.

```
lein garden auto
```

### Run application:

```
lein clean
lein dev
```

shadow-cljs will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:8280](http://localhost:8280).

### Run tests:

(There aren't any actual tests right now)

Install karma and headless chrome

```
npm install -g karma-cli
```

And then run your tests

```
lein clean
lein run -m shadow.cljs.devtools.cli compile karma-test
karma start --single-run --reporters junit,dots
```

## License - Apache 2.0

Copyright 2019 Omnyway Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[Apache License](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License
