# --- !Ups

CREATE TABLE "public"."role" (
    "id" serial,
    "role_name" varchar(100),
    "oauth_client_id" BIGINT NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    PRIMARY KEY ("id")
);
alter table "role" add constraint "role_client_fk" foreign key("oauth_client_id") references "oauth_clients"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs
DROP TABLE role