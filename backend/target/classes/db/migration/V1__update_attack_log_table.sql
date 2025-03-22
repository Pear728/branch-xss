-- 修改attack_log表，确保ID正确设置为自增主键
ALTER TABLE attack_log MODIFY id BIGINT NOT NULL AUTO_INCREMENT;
 