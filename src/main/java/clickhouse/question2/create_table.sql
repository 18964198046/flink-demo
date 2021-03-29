1. 创建表
CREATE TABLE USER
(
`date` Date, `id` UInt8, `name` String
)
ENGINE = MergeTree
PARTITION BY toYYYYMM(date)
ORDER BY id