- I am building an insurance claims app for the HCLTech/Chubb take-home assessment. Customers should be able to report a motor or property incident, track their claim and reply when the claims team asks for more information. Staff need to pick up claims, assess them and settle or reject them, with a view of their workload.

- I want to use Angular, Java/Spring Boot, PostgreSQL and Kafka, and package it so someone else can run it locally.

- I have attached the project brief, the role and assessment slides, and a video about the repo structure. Please go through those and tell me what we need to cover, anything that's unclear, and how you would approach the build. Let's agree on a plan before we start coding.

- Finish the main flow properly than have lots of half-working features.

- Consider separate services for claims and reporting, but I would like to understand whether that's worth the extra setup. Compare that with keeping everything in one application.

- Give the design and explain where REST and Kafka fit. Write down why we're choosing this approach so I can come back to it later.

- Start with the backend model. A customer reports a claim, someone from the claims team picks it up and they may need to ask the customer for more information before deciding.

- Work out the tables and status changes around that flow. We need the history of what happened to each claim. Show me the model first, then add the migrations and business rules.

- Add the APIs for these flows now? Keep them consistent and update the API docs alongside the code. Customers should only see their own claims and staff shouldn't be able to change a claim assigned to someone else.

- Also check what happens if someone clicks Submit twice, or two officers try to pick up the same claim.

- Reporting through Kafka, what happens if we save a claim and then fail to publish the message. How should we handle that?

- Reporting can be a little behind, but we shouldn't lose the claim. Suggest an approach and talk me through the failure cases before implementing it.

- Test this by stopping Kafka, saving a claim and then bringing Kafka back? Check that the same claim eventually shows up in reporting.

- Time to connect the Angular app. We need a customer view and a staff view in the same application. The brief also mentions micro-frontends, so let's work out a shell and split the two features sensibly.

- What happens to the selected claim after an update or refresh? Check for stale details and make sure leaving the page stops any background polling.

- Let's add tests around the parts most likely to break: status changes, permissions, duplicate submissions and two people updating the same claim. I would also like a browser test for the customer-to-staff flow.

- Have we actually checked the Kafka connection, or just tested our own code with mocked messages?

- I need to hand this over so someone else can run it on their laptop. Set it up with Docker images and Compose, and make sure it also works with Podman on my Mac. They shouldn't need Java or Node installed separately.

- Add the commands to the README and try them from the packaged project. Keep the database data when the containers are stopped and started again.

- What files do I send them, and will the same images work on different environments?

- Before we call this finished, go back through the code and look for things we've missed. Start with anything that could let the wrong person update a claim, lose data or leave a claim in the wrong state.

- Show me the issues in order of importance. Fix confirmed bugs one at a time and check the fix, rather than doing a big refactor at this stage.

- Check that the sample data loads safely on later restarts too. Don't clear the claims we've already entered to get it working.

- Keep a short record of the choices we made, what changed along the way and any AI suggestions we had to correct. Link the assessment requirements to the parts of the project so the reviewer can find them.

- I need to be able to explain this in the interview. Walk me through one claim, from entering it in Angular to saving it in the backend and updating the dashboard. Show me the relevant code as we go.

- Then help me prepare for questions about the design choices, what could go wrong and what I would change for a production system.
