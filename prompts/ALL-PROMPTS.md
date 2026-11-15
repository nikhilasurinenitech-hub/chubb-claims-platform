# Developer prompts

## 01. Requirements and delivery plan

```text
I'm building an insurance claims app for the HCLTech/Chubb take-home assessment. Customers should be able to report a motor or property incident, track their claim and reply when the claims team asks for more information. Staff need to pick up claims, assess them and settle or reject them, with a view of their workload.

I want to use Angular, Java/Spring Boot, PostgreSQL and Kafka, and package it so someone else can run it locally.

I've attached the project brief, the role and assessment slides, and a video about the repo structure. Please go through those and tell me what we need to cover, anything that's unclear, and how you'd approach the build. Let's agree on a plan before we start coding.
```

### Follow-up prompts

```text
What would you leave out if we're running short of time? I'd rather finish the main flow properly than have lots of half-working features.
```

## 02. Architecture and design decisions

```text
How would you structure this? I'm considering separate services for claims and reporting, but I'd like to understand whether that's worth the extra setup. Compare that with keeping everything in one application.

Sketch the design and explain where REST and Kafka fit. Write down why we're choosing this approach so I can come back to it later.
```

### Follow-up prompts

```text
Do we need all these components for this project? Have another look and see what we can simplify.
```

## 03. Domain and SQL model

```text
Let's start with the backend model. A customer reports a claim, someone from the claims team picks it up, and they may need to ask the customer for more information before deciding.

Work out the tables and status changes around that flow. We'll also need the history of what happened to each claim. Show me the model first, then add the migrations and business rules.
```

### Follow-up prompts

```text
One thing to check: the amount requested, the reserve and the final settlement aren't necessarily the same. Are we handling that, including different currencies?
```

## 04. REST contracts and security

```text
Can you add the APIs for these flows now? Keep them consistent and update the API docs alongside the code. Customers should only see their own claims, and staff shouldn't be able to change a claim assigned to someone else.

Also check what happens if someone clicks Submit twice, or two officers try to pick up the same claim.
```

### Follow-up prompts

```text
Try calling those endpoints directly with the wrong user. I want to make sure the permissions hold even without the frontend checks.
```

## 05. Kafka and reporting

```text
Let's hook up reporting through Kafka. My main concern is what happens if we save a claim and then fail to publish the message. How should we handle that?

Reporting can be a little behind, but we shouldn't lose the claim or count it twice when a message gets retried. Suggest an approach and talk me through the failure cases before implementing it.
```

### Follow-up prompts

```text
Can we test this by stopping Kafka, saving a claim and then bringing Kafka back? Check that the same claim eventually shows up in reporting.
```

## 06. Angular frontend and state

```text
Time to connect the Angular app. We need a customer view and a staff view in the same application. The brief also mentions micro-frontends, so let's work out a shell and split the two features sensibly.

Share the bits that are actually common. For now, focus on getting a claim through the screens and keep the state handling easy to follow.
```

### Follow-up prompts

```text
What happens to the selected claim after an update or refresh? Check for stale details, and make sure leaving the page stops any background polling.
```

## 07. User experience and accessibility

```text
Can you tidy up the screens now? The forms and claim history should be easy to follow, with a clear next step. Check it on a smaller screen and with the keyboard too. If a request fails, keep what the user has typed and show an error they can act on.
```

### Follow-up prompts

```text
The workload numbers need a clearer explanation. Waiting is part of Open, right? Make sure that comes across and that users know reporting can take a moment to catch up.
```

## 08. Testing the important flows

```text
Let's add tests around the parts most likely to break: status changes, permissions, duplicate submissions and two people updating the same claim. I'd also like a browser test for the customer-to-staff flow.

Use real database checks where mocks would hide a problem. Run the tests and tell me what's covered and what we still haven't checked.
```

### Follow-up prompts

```text
Have we actually checked the Kafka connection, or just tested our own code with mocked messages?
```

## 09. Docker images and local setup

```text
I need to hand this over so someone else can run it on their laptop. Set it up with Docker images and Compose, and make sure it also works with Podman on my Mac. They shouldn't need Java or Node installed separately.

Add the commands to the README and try them from the packaged project. Keep the database data when the containers are stopped and started again.
```

### Follow-up prompts

```text
What files do I send them, and will the same images work on an Intel laptop and an Apple Silicon Mac?
```

## 10. Review and debugging

```text
Before we call this finished, go back through the code and look for things we've missed. Start with anything that could let the wrong person update a claim, lose data or leave a claim in the wrong state.

Show me the issues in order of importance. Fix confirmed bugs one at a time and check the fix, rather than doing a big refactor at this stage.
```

### Follow-up prompts

```text
If the containers are healthy but the browser gets an error after a restart, where would you look first? Trace the request through the proxy as well.
```

```text
Check that the sample data loads safely on later restarts too. Don't clear the claims we've already entered to get it working.
```

## 11. Design notes and AI journal

```text
Let's bring the docs up to date. Keep a short record of the choices we made, what changed along the way and any AI suggestions we had to correct. Link the assessment requirements to the relevant parts of the project so the reviewer can find them.

Only list checks we've actually run. Anything still planned should be clear about that.
```

### Follow-up prompts

```text
Does the README still match the app? Check the setup steps and remove anything that makes the project sound more complete than it is.
```

## 12. Walkthrough and interview preparation

```text
I need to be able to explain this in the interview. Walk me through one claim, from entering it in Angular to saving it in the backend and updating the dashboard. Show me the relevant code as we go.

Then help me prepare for questions about the design choices, what could go wrong and what I'd change for a production system.
```

### Follow-up prompts

```text
Ask me one question at a time. If I miss something, help me find it in the code rather than just giving me an answer to memorise.
```
