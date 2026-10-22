-- Sprint OS: responsável, auditoria e comentários. MySQL 8+.
-- MANUAL: selecione o banco de homologação com USE <nome_do_banco> antes de executar.
-- Pare a aplicação, faça backup e execute primeiro em uma cópia do banco.
-- DDL no MySQL realiza commits implícitos: não há rollback global desta migração.
-- Não executar no perfil H2 de testes. Não é carregado automaticamente pelo Spring.
-- Reexecução suportada: consulta metadados antes de acrescentar colunas/índices/FKs.
SELECT DATABASE() AS banco_selecionado;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'os' AND COLUMN_NAME = 'responsavel_id'), 'SELECT 1', 'ALTER TABLE `os` ADD COLUMN `responsavel_id` BIGINT NULL');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'os' AND COLUMN_NAME = 'data_fim'), 'SELECT 1', 'ALTER TABLE `os` ADD COLUMN `data_fim` DATE NULL');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'historico_os' AND COLUMN_NAME = 'tipo_evento'), 'SELECT 1', 'ALTER TABLE `historico_os` ADD COLUMN `tipo_evento` VARCHAR(40) NULL');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'historico_os' AND COLUMN_NAME = 'descricao'), 'SELECT 1', 'ALTER TABLE `historico_os` ADD COLUMN `descricao` VARCHAR(4000) NULL');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'historico_os' AND COLUMN_NAME = 'responsavel_anterior_id'), 'SELECT 1', 'ALTER TABLE `historico_os` ADD COLUMN `responsavel_anterior_id` BIGINT NULL');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'historico_os' AND COLUMN_NAME = 'novo_responsavel_id'), 'SELECT 1', 'ALTER TABLE `historico_os` ADD COLUMN `novo_responsavel_id` BIGINT NULL');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'historico_os' AND COLUMN_NAME = 'de' AND (DATA_TYPE IN ('text','mediumtext','longtext') OR CHARACTER_MAXIMUM_LENGTH >= 4000)), 'SELECT 1', 'ALTER TABLE `historico_os` MODIFY COLUMN `de` VARCHAR(4000) NULL');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'historico_os' AND COLUMN_NAME = 'para' AND (DATA_TYPE IN ('text','mediumtext','longtext') OR CHARACTER_MAXIMUM_LENGTH >= 4000)), 'SELECT 1', 'ALTER TABLE `historico_os` MODIFY COLUMN `para` VARCHAR(4000) NULL');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

CREATE TABLE IF NOT EXISTS `comentario_os` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `conteudo` VARCHAR(4000) NOT NULL,
    `data_criacao` DATETIME(6) NOT NULL,
    `data_edicao` DATETIME(6) NULL,
    `os_id` BIGINT NOT NULL,
    `autor_id` BIGINT NOT NULL,
    `editado_por_id` BIGINT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'os' AND COLUMN_NAME = 'responsavel_id' AND REFERENCED_TABLE_NAME = 'usuarios' AND REFERENCED_COLUMN_NAME = 'id'), 'SELECT 1', 'ALTER TABLE `os` ADD CONSTRAINT `fk_sprint_os_responsavel` FOREIGN KEY (`responsavel_id`) REFERENCES `usuarios` (`id`)');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'historico_os' AND COLUMN_NAME = 'responsavel_anterior_id' AND REFERENCED_TABLE_NAME = 'usuarios' AND REFERENCED_COLUMN_NAME = 'id'), 'SELECT 1', 'ALTER TABLE `historico_os` ADD CONSTRAINT `fk_sprint_hist_responsavel_anterior` FOREIGN KEY (`responsavel_anterior_id`) REFERENCES `usuarios` (`id`)');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'historico_os' AND COLUMN_NAME = 'novo_responsavel_id' AND REFERENCED_TABLE_NAME = 'usuarios' AND REFERENCED_COLUMN_NAME = 'id'), 'SELECT 1', 'ALTER TABLE `historico_os` ADD CONSTRAINT `fk_sprint_hist_novo_responsavel` FOREIGN KEY (`novo_responsavel_id`) REFERENCES `usuarios` (`id`)');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'comentario_os' AND COLUMN_NAME = 'os_id' AND REFERENCED_TABLE_NAME = 'os' AND REFERENCED_COLUMN_NAME = 'id'), 'SELECT 1', 'ALTER TABLE `comentario_os` ADD CONSTRAINT `fk_sprint_comentario_os` FOREIGN KEY (`os_id`) REFERENCES `os` (`id`)');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'comentario_os' AND COLUMN_NAME = 'autor_id' AND REFERENCED_TABLE_NAME = 'usuarios' AND REFERENCED_COLUMN_NAME = 'id'), 'SELECT 1', 'ALTER TABLE `comentario_os` ADD CONSTRAINT `fk_sprint_comentario_autor` FOREIGN KEY (`autor_id`) REFERENCES `usuarios` (`id`)');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'comentario_os' AND COLUMN_NAME = 'editado_por_id' AND REFERENCED_TABLE_NAME = 'usuarios' AND REFERENCED_COLUMN_NAME = 'id'), 'SELECT 1', 'ALTER TABLE `comentario_os` ADD CONSTRAINT `fk_sprint_comentario_editor` FOREIGN KEY (`editado_por_id`) REFERENCES `usuarios` (`id`)');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'historico_os' AND INDEX_NAME = 'idx_sprint_hist_os_data'), 'SELECT 1', 'CREATE INDEX `idx_sprint_hist_os_data` ON `historico_os` (`os_id`, `data_alteracao`, `id`)');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'historico_os' AND INDEX_NAME = 'idx_sprint_hist_evento_usuario'), 'SELECT 1', 'CREATE INDEX `idx_sprint_hist_evento_usuario` ON `historico_os` (`os_id`, `tipo_evento`, `usuario_id`)');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'comentario_os' AND INDEX_NAME = 'idx_sprint_comentario_os_data'), 'SELECT 1', 'CREATE INDEX `idx_sprint_comentario_os_data` ON `comentario_os` (`os_id`, `data_criacao`, `id`)');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

SET @sprint_os_ddl = IF(EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'os' AND INDEX_NAME = 'idx_sprint_os_emissao_cliente'), 'SELECT 1', 'CREATE INDEX `idx_sprint_os_emissao_cliente` ON `os` (`data_emissao`, `cliente_id`)');
PREPARE sprint_os_stmt FROM @sprint_os_ddl;
EXECUTE sprint_os_stmt;
DEALLOCATE PREPARE sprint_os_stmt;

-- Compatibilidade legada: o vínculo antigo era o criador, também usado como responsável efetivo.
-- Não registrar evento de atribuição retrospectivo: nenhum usuário realizou essa ação agora.
UPDATE `os`
SET `responsavel_id` = `usuario_id`
WHERE `responsavel_id` IS NULL AND `usuario_id` IS NOT NULL;

-- NÃO alterar tipo_evento, IDs, datas, de/para ou usuário dos históricos já existentes.
-- Eventos legados nulos são classificados para exibição/filtro pelo backend.
-- NÃO inventar data_fim para OS antigas: a data real de encerramento não foi armazenada.
SELECT COUNT(*) AS os_sem_responsavel FROM `os` WHERE `responsavel_id` IS NULL;
SELECT COUNT(*) AS historicos_legados_preservados FROM `historico_os` WHERE `tipo_evento` IS NULL;
SELECT COUNT(*) AS comentarios FROM `comentario_os`;
