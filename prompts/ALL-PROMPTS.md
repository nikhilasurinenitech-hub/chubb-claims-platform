# Developer prompts

## 01. Requirements and delivery plan

```text
I'm building the claims project described in the attached assessment brief. Please go through the brief, the assessment slides and the repository video first.

We need Angular on the frontend and Java/Spring Boot on the backend. PostgreSQL and Kafka are also agreed. Summarise the main user journeys, what we must deliver, and anything the brief leaves open.

Before writing code, give me a practical implementation plan. Prioritise a working end-to-end flow and the areas that carry more assessment points. Call out assumptions and anything we should leave out of this version.
```

### Follow-up prompts

```text
This looks like a lot for a take-home. What is the smallest useful version we can finish properly, and what would you build first?
```

## 02. Architecture and design decisions

```text
Let's work out the architecture before we start implementing. Would you keep this as one application or split it into a small number of services?

Compare the options for this project and explain the trade-offs. We need REST and Kafka, but I want both to have a clear purpose. Show the main components, who owns the data, and how a claim moves through the system.

Keep the structure straightforward and write down the decisions so I can explain them later.
```

### Follow-up prompts

```text
Can we simplify this further? Which parts really benefit from being separate, and which parts should stay together?
```

## 03. Domain and SQL model

```text
Start with the claim model and its lifecycle. We need claimants to report incidents, officers to pick up and assess claims, an information-request loop, and a final settlement or rejection.

Suggest the tables and relationships, then implement the model and database migrations. Keep the status rules in the backend. Think about ownership, assignment, history and money values, including the different currencies.

Please explain the important rules before wiring this into controllers or screens.
```

### Follow-up prompts

```text
What invalid status changes could slip through if someone calls the API directly? Also, are we keeping the requested amount, assessed reserve and final settlement separate?
```

## 04. REST contracts and security

```text
Let's add the APIs for the claimant and staff flows. Keep the endpoints and response shapes consistent and document them in OpenAPI.

Make sure a claimant only sees their own claims and an officer can only progress work assigned to them. Validate requests on the server and return useful errors.

Also think through duplicate submissions and two people trying to update the same claim. Explain how the frontend should handle those cases.
```

### Follow-up prompts

```text
Check the API without relying on the UI restrictions. What happens if I try another claimant's ID, use the wrong role, or submit an update from an old screen?
```

## 05. Kafka and reporting

```text
Now connect claim changes to the reporting side through Kafka. Reporting can update a little later, but saving a valid claim should not fail just because Kafka is unavailable.

Suggest a reliable way to handle that database-and-message boundary. Account for retries, repeated messages and messages arriving out of order. Then implement the approach and explain how we would recover reporting after a failure.
```

### Follow-up prompts

```text
Suppose the claim is saved but publishing fails. Or the message is published twice. Walk me through what happens in our implementation and how we can check it.
```

## 06. Angular frontend and state

```text
Build the Angular frontend for both user types in one workspace. Give the claimant and staff flows clear boundaries, with shared components where that makes sense.

The assessment mentions micro-frontends, so propose a sensible shell and feature structure without making the local setup difficult. Keep API calls and state handling easy to follow.

Let's get the main workflow working first, including loading states, errors and refreshing data after an action.
```

### Follow-up prompts

```text
Is the shared code doing too much? Keep business decisions on the backend, and check that moving between pages does not leave polling or stale state behind.
```

## 07. User experience and accessibility

```text
The main screens are working. Please review the UI and make it feel consistent and easy to use.

Focus on the claim form, the staff workqueue and the claim history. Make the next action clear, improve validation and empty/error states, and check keyboard access and smaller screens. Keep entered text when a request fails.
```

### Follow-up prompts

```text
The dashboard numbers might be confusing. Can we make it clear what Open, Waiting and Closed mean, and why reporting may take a moment to update?
```

## 08. Testing the important flows

```text
Add meaningful tests around the important behaviour rather than testing every getter or copying the implementation into assertions.

Cover the claim lifecycle, permissions, duplicate requests and conflicting updates. Include database integration checks and frontend tests for the key journeys. We should also check that Kafka updates reporting correctly.

Run the relevant tests, fix any failures, and summarise what was actually verified and what remains untested.
```

### Follow-up prompts

```text
Which failures would our unit tests miss? Pick the highest-risk integration gaps and suggest a practical way to test them.
```

## 09. Docker images and local setup

```text
Make the project easy for the examiner to run using Docker or Podman. They should not need to install Java or Node separately.

Include Compose, startup checks and persistent storage. Give me a source-build option and a way to load the prepared images. Account for Apple Silicon and Intel/AMD machines.

Write clear commands in the README for starting, checking logs, stopping and restarting. Please verify the instructions against the packaged project.
```

### Follow-up prompts

```text
What exactly do I need to send the examiner? Check what is inside the image bundle, what still gets downloaded, and whether saved claims survive a restart.
```

## 10. Review and debugging

```text
Please review what we have before the final handoff. Look for bugs in permissions, status changes, concurrent updates, error handling and data consistency.

Start with the most important findings and point me to the relevant code. For a confirmed issue, reproduce it, make a focused fix and add a regression check where it helps. Avoid a broad rewrite just to change the style.
```

### Follow-up prompts

```text
The service health checks pass, but a request through the browser fails after a container restart. Trace the full request path before changing the application logic.
```

```text
Check startup and sample-data loading as well. Will this still work after another restart tomorrow? Keep the claims already entered in the running demo.
```

## 11. Design notes and AI journal

```text
Update the project documentation as we go. I need the plan, important design choices, assumptions and AI-assisted iterations to be easy to find.

Record what changed, why we changed it, and the checks we actually ran. Include mistakes and corrections when they explain an important decision. Keep the prompt files separate from the implementation journal.

Finally, map the assessment areas to the relevant code or evidence so the reviewer can navigate the project.
```

### Follow-up prompts

```text
Review the documentation for claims stronger than the evidence. Make sure it is clear what works locally, what was only validated statically, and what is future work.
```

## 12. Walkthrough and interview preparation

```text
Help me prepare a walkthrough of the finished project. Start with the business problem and demonstrate a claim from submission to a final decision using the two user roles.

Then follow one action through the frontend, backend, database and reporting flow. Explain the main design choices and the alternatives we considered in plain language.

Give me likely technical questions based on our actual implementation, and point out the areas I should understand well before the interview.
```

### Follow-up prompts

```text
Ask me the questions one at a time. If my explanation misses something, point me to the relevant code and help me work through it.
```
