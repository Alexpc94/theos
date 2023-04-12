-- TABLE PERSONAL ADD (telefono and newcodigoper)
-- newcodigoper -> equal (actual code) socio
ALTER TABLE public.personal
ADD COLUMN telefono character varying(25),
ADD COLUMN newcodigoper character varying(25) NOT NULL DEFAULT '0';
-- actestado added
ALTER TABLE public.personal ADD COLUMN actestado integer NOT NULL DEFAULT 1;
UPDATE public.personal SET actestado = 0 WHERE codigoper = 0;