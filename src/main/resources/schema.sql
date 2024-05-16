-- DROP TABLE IF EXISTS messages;

CREATE TABLE if NOT EXISTS messages (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      dateTime TIMESTAMP NOT NULL,
      requestId VARCHAR(50),
      trackingId VARCHAR(50),
      filePath VARCHAR(255),
      code INT,
      status VARCHAR(50),
      type VARCHAR(50)
);
