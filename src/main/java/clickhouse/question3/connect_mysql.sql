
1.创建与MySQL的关联表

CREATE TABLE mysql_user
(
`id` UInt32, `name` String, `age` UInt32
)
ENGINE = MySQL('172.16.97.71:3306', 'bigdata', 'user', 'root', 'yfx')


2.查询关联表

SELECT * FROM mysql_user