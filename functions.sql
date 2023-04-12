-- Function: public.addpersona(text, integer, text, text, text, text, text, text, date, date, text, integer)

-- DROP FUNCTION public.addpersona(text, integer, text, text, text, text, text, text, date, date, text, integer);

CREATE OR REPLACE FUNCTION public.addpersona(
    xci text,
    xcodigoper integer,
    xnombre text,
    xap text,
    xam text,
    xemail text,
    xecivil text,
    xgenero text,
    fenac date,
    feing date,
    xcliente text,
    xconyuge integer,
    xnewcodigoper text,
    xtelefono text)
  RETURNS text AS
$BODY$
	DECLARE 
		xcodper int;
		xcodigoper int;
	BEGIN
		
		insert into personal(conyuge,codigoper,nombre,ap,am,email,ecivil,genero,fnac,fingreso,newcodigoper,telefono) values($12,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12);		
		select max(codper) into xcodper from personal;
		insert into datosfac(ci,codper,cliente) values($1,xcodper,$11);
		if($1<>'-')then
			insert into datosper(ci,codper) values($1,xcodper);			
		end if;
		update general set nroaccion=(nroaccion + 1) where codg=1;
        RETURN '0';
		EXCEPTION
			when unique_violation then 
			return '1';
			when others then 
			return '2';
    END;
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.addpersona(text, integer, text, text, text, text, text, text, date, date, text, integer)
  OWNER TO postgres;
