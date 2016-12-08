# --- !Ups

ALTER TABLE "public"."policy" DROP CONSTRAINT "service_id_fk";
ALTER TABLE "public"."policy" ADD CONSTRAINT "client_id_fk" FOREIGN KEY ("service_id") REFERENCES "public"."oauth_clients"("id");


# --- !Downs


ALTER TABLE "public"."policy" ADD CONSTRAINT "service_id_fk" FOREIGN KEY ("service_id") REFERENCES "public"."service"("id");
ALTER TABLE "public"."policy" DROP CONSTRAINT "client_id_fk"
