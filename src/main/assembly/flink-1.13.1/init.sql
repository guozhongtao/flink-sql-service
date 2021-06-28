CREATE CATALOG myhive WITH (
    'type' = 'hive',
    'default-database' = 'test',
    'hive-conf-dir' = '/Users/wangkai/apps/install/hive-2.3.8-client/conf'
);
-- set the HiveCatalog as the current catalog of the session
USE CATALOG myhive;
