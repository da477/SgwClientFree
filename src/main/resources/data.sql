DELETE FROM messages;

INSERT INTO messages (dateTime, requestId, trackingId, filePath, code, status, type)
VALUES ('2021-01-01 10:00:00', 'req-123', 'track-123', '', 200, 'NEW', 'GET'),
       ('2024-01-01 12:00:00', 'req-1235', 'track-1235', '', 204, 'NEW', 'STATEMENT'),
       ('2021-02-01 11:00:00', 'req-124', 'track-124', '/path/to/another/file', 404, 'ERROR', 'PING'),
       ('2024-05-05 19:11:46', 'a7da3a7e32', 'wwww', '', 403, 'ERROR', 'PING'),
       ('2024-05-06 11:08:37', 'fd4d83579', 'af64ee85', '', 204, 'NEW', 'PING'),
       ('2024-05-06 16:40:10', '7e2e6796b', 'af64ee85', '20240506164011626.xml', 204, 'DELETED', 'GET'),
       ('2024-05-06 16:49:43', '309b8268323', 'ef5b9dc6', '20240506164944575.xml', 204, 'DELETED', 'GET'),
       ('2024-05-06 16:57:30', 'ae03af43b', 'a60d19bc', '', 204, 'NEW', 'PING'),
       ('2024-05-06 16:58:31', '6cfbca742', 'a60d19bc', '20240506165831868.xml', 204, 'DELETED', 'GET'),
       ('2024-05-06 17:07:40', 'bbf17dec8', 'c05f0720', '', 204, 'SENT', 'STATEMENT');
