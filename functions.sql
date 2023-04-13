-- Function: public.addpersona(text, integer, text, text, text, text, text, text, date, date, text, integer, text, text)

-- DROP FUNCTION public.addpersona(text, integer, text, text, text, text, text, text, date, date, text, integer, text, text);

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
		
		insert into personal(conyuge,codigoper,nombre,ap,am,email,ecivil,genero,fnac,fingreso,newcodigoper,telefono) values($12,$2,$3,$4,$5,$6,$7,$8,$9,$10,$13,$14);		
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
ALTER FUNCTION public.addpersona(text, integer, text, text, text, text, text, text, date, date, text, integer, text, text)
  OWNER TO postgres;

  
  
-- Function: public.modpersona(text, integer, text, text, text, text, text, text, date, date, integer, integer)

-- DROP FUNCTION public.modpersona(text, integer, text, text, text, text, text, text, date, date, integer, integer);

CREATE OR REPLACE FUNCTION public.modpersona(
    xci text,
    xcodper integer,
    xnombre text,
    xap text,
    xam text,
    xemail text,
    xecivil text,
    xgenero text,
    fenac date,
    feing date,
    xconyuge integer,
    xcodigoper integer,
    xnewcodigoper text,
    xtelefono text)
  RETURNS text AS
$BODY$
	DECLARE 
		xcant int;
	BEGIN
		
		update personal
		set nombre=$3,
		    ap=$4,
		    am=$5,
		    email=$6,	
		    ecivil=$7,
		    genero=$8, 
		    fnac=$9,
		    fingreso=$10,
		    conyuge=$11,
		    codigoper=$12,
		    newcodigoper=$13,
		    telefono=$14
		where codper=$2;
		
		if($1<>'-')then
			select count(codper) into xcant from datosper where(codper=$2);
			if( xcant=null) then xcant=0; end if;		
			if(xcant=0)then
				insert into datosper(ci,codper) values($1,$2);
			else
				update datosper
				set ci=$1
				where codper=$2;
			end if;	
		end if;
		
        RETURN '0';
		EXCEPTION
			when others then 
			return '1';
    END;
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.modpersona(text, integer, text, text, text, text, text, text, date, date, integer, integer, text, text)
  OWNER TO postgres;
  