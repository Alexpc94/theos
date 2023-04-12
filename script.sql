-- TABLE PERSONAL ADD (telefono and newcodigoper)
-- newcodigoper -> equal (actual code) socio
ALTER TABLE public.personal
ADD COLUMN telefono character varying(25),
ADD COLUMN newcodigoper character varying(25) NOT NULL DEFAULT '0';