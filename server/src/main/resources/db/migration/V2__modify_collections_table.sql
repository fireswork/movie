-- 删除旧的movie_id字段
ALTER TABLE collections DROP COLUMN movie_id;

-- 添加新的movie_ids字段
ALTER TABLE collections ADD COLUMN movie_ids VARCHAR(1000) NOT NULL DEFAULT ''; 