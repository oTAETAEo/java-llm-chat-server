INSERT INTO members (id, nickname)
VALUES (1, 'temporary-user')
ON CONFLICT (id) DO UPDATE
SET nickname = EXCLUDED.nickname;

SELECT setval(
    pg_get_serial_sequence('members', 'id'),
    GREATEST((SELECT COALESCE(MAX(id), 1) FROM members), 1)
);
