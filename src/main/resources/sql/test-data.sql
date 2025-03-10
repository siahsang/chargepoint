-- Insert a record where the driver is allowed access to the station
INSERT INTO acl_entity (driver_token, station_id, is_allowed)
VALUES ('driver_token_test_123456', 'd546676f-0786-4fb7-a32c-17618a2b9e38', true);

-- Insert a record where the driver is denied access to the station
INSERT INTO acl_entity (driver_token, station_id, is_allowed)
VALUES ('driver_token_test_7890123', 'd546676f-0786-4fb7-a32c-17618a2b9e38', false);

-- Insert another allowed record
INSERT INTO acl_entity (driver_token, station_id, is_allowed)
VALUES ('driver_token_test_abc_dsfsdfs', 'd546676f-0786-4fb7-a32c-17618a2b9e38', true);

-- Insert a record with a different driver and station
INSERT INTO acl_entity (driver_token, station_id, is_allowed)
VALUES ('driver_token_test_3423432', 'd546676f-0786-4fb7-a32c-17618a2b9e38', true);