ALTER TABLE tickets
    ADD COLUMN requester_id BIGINT,
    ADD COLUMN assignee_id BIGINT;

ALTER TABLE tickets
    ADD CONSTRAINT fk_tickets_requester
        FOREIGN KEY (requester_id)
        REFERENCES users(id);

ALTER TABLE tickets
    ADD CONSTRAINT fk_tickets_assignee
        FOREIGN KEY (assignee_id)
        REFERENCES users(id);