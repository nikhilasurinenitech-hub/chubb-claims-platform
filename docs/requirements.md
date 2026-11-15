# Requirements and source interpretation

The user's request controls implementation. The attached documents and video were used as reference material.

## User decisions

Angular frontend and Java/Spring Boot backend were required. SQL was allowed. The user explicitly approved PostgreSQL, Kafka, Podman/Compose and supporting migration, federation, serving and test libraries. The two-service design was approved, and remaining architecture choices were delegated. The requested delivery location is Downloads, with a runnable project and explanations for the panel.

## Brief requirements

The supplied Fullstack Developer brief describes motor and property claims across six markets, two user types in one Angular application, Java/Spring Boot as an allowed backend and both REST/HTTP and Kafka communication. It deliberately leaves service boundaries, data model and priorities to the candidate. It asks for a local application, meaningful Git history, an AI journal and a 30–60-minute panel walkthrough. It describes a 2–3-hour target and a five-hour cap, with backend quality taking priority.

## Screenshot rubric

The scoring image assigns 65 backend points, 45 frontend points and 25 AI-collaboration points. Planning/design and clean architecture each carry 15 backend points; MFE architecture carries 15 frontend points. The evidence map follows these areas without claiming a guaranteed score.

## Video guidance

The five-minute repository video recommends a clear README, layered source, dedicated tests, configuration and migrations, an API contract, Compose, decision/assumption notes, saved prompts and an AI journal. It shows an example repository name; it does not impose that name on this project.

The source layout follows these principles with conventional Maven main/java, main/resources and test/java folders inside the backend modules. Deployment artifacts, contracts and documentation are easy to locate at the root.

## Explicit boundaries

Sample identities, market configuration and claims are fictional. The brief does not define actual policy coverage rules, production identity, payment integrations, file storage or exchange-rate policy. These are documented assumptions or deferred integrations rather than invented external facts.
