# Migrations Flyway

## Trou de numérotation V16 → V106

Le commit `d1b06db` (23/07/2026, "Introduce AI-assisted provider profile creation")
a introduit par erreur `V106__remove_testimonials_eventtype.sql` et
`V107__create_generations_ia.sql` au lieu de `V17`/`V18` (erreur de saisie).

Ces deux migrations étaient déjà appliquées en staging/prod au moment de la
découverte de l'erreur. Flyway ne requiert pas de numérotation continue, et
renuméroter rétroactivement aurait nécessité de corriger `flyway_schema_history`
sur tous les environnements concernés — jugé trop risqué pour un problème
purement cosmétique. La séquence continue donc à partir de `V108`.
