# --- !Ups


create sequence s_t_user_id;
create sequence s_t_order_id;
create sequence s_t_orderlines_id;

create table t_user(
id INT default nextval ('s_t_user_id'),
login varchar(128),
pass varchar(128),
email varchar(128),
admin int,
customer_id int
);


create table t_order (
id int DEFAULT nextval('s_t_order_id'),
ref  VARCHAR(128),
fk_soc  int,
order_date DATE,
date_creation timestamp,
date_modif timestamp,
fk_uther_author int,
fk_statut  int,
tva  DOUBLE PRECISION,
total_ht DOUBLE PRECISION,
total_ttc DOUBLE PRECISION,
note VARCHAR(255),
sent boolean,
sent_date timestamp
);

create table t_orderlines (
  id    INT DEFAULT nextval('s_t_orderlines_id'),
  fk_order_id INT,
  product_id  INT,
  product_ref VARCHAR(128),
  label VARCHAR(128),
  tva DOUBLE PRECISION,
  qty  DOUBLE PRECISION,
  unity  VARCHAR(128),
  prix_ht  DOUBLE PRECISION,
  prix_ttc  DOUBLE PRECISION
);



create sequence s_t_supplier_id;
create table t_supplier (
     id INT default nextval ('s_t_supplier_id'),
     name  varchar(128),
     address  varchar(128),
     tel  varchar(128),
     email varchar(128)
);

create sequence s_t_customer_discount_id;
create table t_customer_discount(
    id INT default nextval ('s_t_customer_discount_id'),
    customer_id int,
    supplier_id int,
    discount DOUBLE PRECISION
);

create table "t_address" ("id" SERIAL PRIMARY KEY,"address" VARCHAR(254),"city" VARCHAR(254),"zip" VARCHAR(254),"country" VARCHAR(254),"fk_company" INTEGER);
create table "t_category" ("id" SERIAL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"parent" INTEGER NOT NULL);
create table "t_company" ("id" SERIAL PRIMARY KEY,"name" VARCHAR(254),"price_level" INTEGER,"tel" VARCHAR(254),"email" VARCHAR(254),"supplier" BOOLEAN NOT NULL,"prospect" BOOLEAN NOT NULL);
create table "t_contact" ("id" SERIAL PRIMARY KEY,"first_name" VARCHAR(254),"name" VARCHAR(254),"position" VARCHAR(254),"tel" VARCHAR(254),"mobile" VARCHAR(254),"email" VARCHAR(254),"fk_company" INTEGER);
create table "t_products" ("id" SERIAL PRIMARY KEY,"reference" VARCHAR(254) NOT NULL,"label" VARCHAR(254) NOT NULL,"description" VARCHAR(254),"image_url" VARCHAR(254),"unity" VARCHAR(254) NOT NULL,"category_id" INTEGER,"supplier_id" INTEGER NOT NULL,"manufacture" VARCHAR(254),"reference_supplier" VARCHAR(254),"multi_price" BOOLEAN NOT NULL,"tva_rate" DOUBLE PRECISION NOT NULL,"price_supplier" DOUBLE PRECISION NOT NULL,"base_price" DOUBLE PRECISION NOT NULL);
alter table "t_address" add constraint "FK_COMPANY_ADDRESS" foreign key("fk_company") references "t_company"("id") on update CASCADE on delete CASCADE;
alter table "t_contact" add constraint "FK_COMPANY_CONTACT" foreign key("fk_company") references "t_company"("id") on update CASCADE on delete CASCADE;


# --- !Downs
alter table "t_address" drop constraint "FK_COMPANY_ADDRESS";
alter table "t_contact" drop constraint "FK_COMPANY_CONTACT";
-- alter table "t_orderlines" drop constraint "FK_ORDER_ID";

drop table "t_category";
drop table "t_products";
drop sequence s_t_user_id;
drop table t_user;
drop sequence s_t_order_id;
drop sequence s_t_orderlines_id;
drop table t_order;
drop table t_orderlines;
drop sequence s_t_supplier_id;
drop table t_supplier;
drop sequence s_t_customer_discount_id;
drop t_customer_discount;
drop table "t_address";
drop table "t_company";
drop table "t_contact";




