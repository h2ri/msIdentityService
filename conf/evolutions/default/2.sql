# --- !Ups

CREATE TABLE "public"."service" (
    "id" serial,
    "name" text,
    "abbreviation" text,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."policy" (
    "id" serial,
    "service_id" integer,
    "label" varchar(100),
    PRIMARY KEY ("id"),
    CONSTRAINT "service_id_fk" FOREIGN KEY ("service_id") REFERENCES "public"."service"("id")
);

CREATE TABLE "public"."role" (
    "id" serial,
    "name" text,
    "parent_id" integer,
    PRIMARY KEY ("id"),
    CONSTRAINT "role_as_parent" FOREIGN KEY ("parent_id") REFERENCES "public".role("id")
);

CREATE TABLE "public"."role_policy" (
    "id" serial,
    "role_id" integer,
    "policy_id" integer,
    PRIMARY KEY ("id"),
    CONSTRAINT "role_id_fk" FOREIGN KEY ("role_id") REFERENCES "public"."role"("id"),
    CONSTRAINT "policy_id_fk" FOREIGN KEY ("policy_id") REFERENCES "public"."policy"("id")
);

CREATE TABLE "public"."account_role" (
    "id" serial,
    "role_id" integer,
    "account_id" integer,
    PRIMARY KEY ("id"),
    CONSTRAINT "role_account_fk" FOREIGN KEY ("role_id") REFERENCES "public"."role"("id"),
    CONSTRAINT "accounts_role_fk" FOREIGN KEY ("account_id") REFERENCES "public"."accounts"("id")
);
CREATE TABLE "public"."department" (
    "id" serial,
    "name" text,
    "code" text,
    PRIMARY KEY ("id")
);


CREATE TABLE "public"."employe" (
    "id" serial,
    "account_id" integer,
    "department_id" integer,
    PRIMARY KEY ("id"),
    CONSTRAINT "account_id_fk" FOREIGN KEY ("account_id") REFERENCES "public"."accounts"("id"),
    CONSTRAINT "department_id_fk" FOREIGN KEY ("department_id") REFERENCES "public"."department"("id")
);

# --- !Downs
DROP TABLE service
DROP TABLE policy
DROP TABLE role
DROP TABLE role_policy
DROP TABLE account_role
DROP TABLE department
DROP TABLE employe

