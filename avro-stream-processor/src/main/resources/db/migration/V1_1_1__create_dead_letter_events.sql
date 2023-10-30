CREATE TABLE public.dead_letter_events (
	id uuid NOT NULL,
	event_key uuid NOT NULL,
	service_id varchar(100) NULL, -- consumer
	event_type varchar(500) NULL,
	topic varchar(50) NULL,
	"partition" int4 NULL,
	partition_offset varchar(50) NULL,
	data_as_json text NULL,
	data_as_avro text NULL,
	data_as_avro_byte text NULL,
	reason text NULL,
	created_at timestamp NULL,
	CONSTRAINT dead_letter_events_pkey PRIMARY KEY (id)
);
