DROP TABLE IF EXISTS "transfer";
DROP TABLE IF EXISTS "account";

CREATE TABLE "account" (
    "id"             BIGSERIAL NOT NULL PRIMARY KEY,
    "account_number" TEXT      NOT NULL UNIQUE,
    "client"         TEXT      NOT NULL,
    "balance"        BIGINT    NOT NULL CHECK ("balance" >= 0) DEFAULT 0,
    "active"         BOOLEAN   NOT NULL DEFAULT TRUE
);

CREATE TABLE "transfer" (
    "id"              BIGSERIAL NOT NULL PRIMARY KEY,
    "from_account_id" BIGINT             REFERENCES "account" ON UPDATE RESTRICT ON DELETE RESTRICT,
    "to_account_id"   BIGINT             REFERENCES "account" ON UPDATE RESTRICT ON DELETE RESTRICT,
    "transfer_date"   TIMESTAMP NOT NULL DEFAULT now(),
    "amount"          BIGINT    NOT NULL CHECK ("amount" > 0),
    CHECK ("from_account_id" <> "to_account_id" AND ("from_account_id" IS NOT NULL OR "to_account_id" IS NOT NULL))
);