CREATE SCHEMA "trackinterview";

CREATE TYPE "trackinterview"."job_status" AS ENUM (
  'intial',
  'application_accepted',
  'interview_scheduled',
  'waiting',
  'ghosted',
  'offer'
);

CREATE TABLE "trackinterview"."users" (
  "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  "email" varchar,
  "fName" varchar,
  "lName" varchar
);

CREATE TABLE "trackinterview"."companies" (
  "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  "name" varchar
);

CREATE TABLE "trackinterview"."recruiters" (
  "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  "first_name" varchar,
  "last_name" varchar,
  "email" varchar,
  "company_id" varchar
);

CREATE TABLE "trackinterview"."comments" (
  "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  "user_id" varchar,
  "job_id" varchar,
  "text" varchar
);

CREATE TABLE "trackinterview"."attachments" (
  "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  "file_name" varchar,
  "file_path" varchar,
  "comment_id" varchar,
  "job_id" varchar
);

CREATE TABLE "trackinterview"."jobs" (
  "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  "company_id" int,
  "job_description" varchar,
  "recruiter_id" int,
  "job_link" varchar,
  "status" trackinterview.job_status,
  "user_id" int UNIQUE NOT NULL,
  "comment_id" int,
  "attachment_id" int,
  "created_at" datetime DEFAULT (now()),
  "updated_at" datetime DEFAULT (now())
);

ALTER TABLE "trackinterview"."jobs" ADD FOREIGN KEY ("company_id") REFERENCES "trackinterview"."companies" ("id");

ALTER TABLE "trackinterview"."jobs" ADD FOREIGN KEY ("recruiter_id") REFERENCES "trackinterview"."recruiters" ("id");

ALTER TABLE "trackinterview"."jobs" ADD FOREIGN KEY ("user_id") REFERENCES "trackinterview"."users" ("id");

ALTER TABLE "trackinterview"."jobs" ADD FOREIGN KEY ("comment_id") REFERENCES "trackinterview"."comments" ("id");

ALTER TABLE "trackinterview"."jobs" ADD FOREIGN KEY ("attachment_id") REFERENCES "trackinterview"."attachments" ("id");
