ALTER TABLE place
  ADD COLUMN is_type_edited INTEGER(1) DEFAULT 0 NOT NULL;

ALTER TABLE survey
  objective_id  INTEGER(1)  DEFAULT 1 NOT NULL;
