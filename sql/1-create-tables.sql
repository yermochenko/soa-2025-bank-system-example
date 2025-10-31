DROP TABLE IF EXISTS "account";

CREATE TABLE "account" (
    "id"             BIGSERIAL NOT NULL PRIMARY KEY,
    "account_number" TEXT      NOT NULL UNIQUE,
    "client"         TEXT      NOT NULL,
    "balance"        BIGINT    NOT NULL CHECK ("balance" >= 0) DEFAULT 0,
    "active"         BOOLEAN   NOT NULL DEFAULT TRUE
);
