# Shared Angular Library

Contains typed API contracts, the in-memory authentication facade, the HTTP boundary and reusable presentation components. Native Federation shares this workspace mapping as a singleton so the shell and remotes use the same identity state and Angular injection tokens. Feature pages own their loading, selection and mutation state; shared presentation components emit events and make no API calls.
