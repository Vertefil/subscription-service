CREATE SEQUENCE subscription_id_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE subscriptions
(
    id           BIGINT                      NOT NULL DEFAULT nextval('subscription_id_seq'),
    user_id      BIGINT                      NOT NULL,
    service_name VARCHAR(255)                NOT NULL,
    status       VARCHAR(50)                 NOT NULL,
    price        NUMERIC(10, 2)              NOT NULL,
    start_date   DATE                        NOT NULL,
    end_date     DATE                        NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE    NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE    NOT NULL,
    cancelled_at TIMESTAMP WITH TIME ZONE,
    suspended_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT pk_subscriptions PRIMARY KEY (id),
    CONSTRAINT chk_dates CHECK (end_date > start_date),
    CONSTRAINT chk_price CHECK (price > 0),
    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CANCELLED', 'EXPIRED'))
);

CREATE INDEX idx_subscriptions_user_id ON subscriptions (user_id);
CREATE INDEX idx_subscriptions_status ON subscriptions (status);
CREATE INDEX idx_subscriptions_end_date ON subscriptions (end_date);