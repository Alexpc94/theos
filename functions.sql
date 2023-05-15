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
  
  
  -- Function: public.activar_socio(integer, date, integer, integer, text, integer)

-- DROP FUNCTION public.activar_socio(integer, date, integer, integer, text, integer);

CREATE OR REPLACE FUNCTION public.activar_socio(
    xcodper integer,
    xfecha date,
    xmes integer,
    xanio integer,
    xlogin text,
    xop integer)
  RETURNS text AS
$BODY$
	DECLARE
		xtransfer int;
		xsw int;
	BEGIN	
		xsw=0;
		if ($6 = 1) then
			--SI EL SOCIO HIZO TRANFERENCIA
			select p.transfer into xtransfer
			from personal p
			where p.codper=$1;
		
			if (xtransfer=0) then
				insert into activosper(codper,fecha,mesini,anioini,login,obs)
				values($1,$2,$3,$4,$5,'ACTIVA SOCIO');

				update personal
				set activo=1,activosw=1,mesini=$3,anioini=$4
				where codper=$1;
			else 
				xsw='2';
			end if;
		end if;
		if ($6 = 2) then
			insert into activosper(codper,fecha,mesini,anioini,login,obs)
				values($1,$2,$3,$4,$5,'DESACTIVAR SOCIO');
			
			update personal
			set activo=0,activosw=0,mesini=0,anioini=0
			where codper=$1;
		end if;
		
        RETURN xsw;
		EXCEPTION
			when others then 
			return '1';
    END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.activar_socio(integer, date, integer, integer, text, integer)
  OWNER TO postgres;
  
  -- Function: public.add_transferencia(date, integer, integer, text, text, integer)

-- DROP FUNCTION public.add_transferencia(date, integer, integer, text, text, integer);

CREATE OR REPLACE FUNCTION public.add_transferencia(
    xfecha date,
    xcodper_ant integer,
    xcodper_nue integer,
    xlogin text,
    xobser text,
    xinterespagar integer)
  RETURNS text AS
$BODY$
DECLARE 
	xaccion integer;
    BEGIN		
		--SELECCIONA accion
		select codigoper into xaccion
		from personal
		where codper=$2;
		
		insert into transferencias(fecha,codper_padre,codper_hijo,login,obser,accion,interes)
			values($1,$2,$3,$4,$5,xaccion,$6);
		
		--INHABILITA A SOCIO ANTIGUO = transfer=si tranfirio su cuenta
		update personal
		set activo=0,transfer=1, codper_transfer=$3
		where codper=$2;
		
		--INHABILITA A SOCIO NUEVO = transfer_sw= si es por tranferecia 
		update personal
		set transfer_sw=1, codper_transfer=$2, codigoper=xaccion
		where codper=$3;
		
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
ALTER FUNCTION public.add_transferencia(date, integer, integer, text, text, integer)
  OWNER TO postgres;
  
  
  -- Function: public.add_transferencia(date, integer, integer, text, text, integer)

-- DROP FUNCTION public.add_transferencia(date, integer, integer, text, text, integer);

CREATE OR REPLACE FUNCTION public.add_transferencia(
    xfecha date,
    xcodper_ant integer,
    xcodper_nue integer,
    xlogin text,
    xobser text,
    xinterespagar integer)
  RETURNS text AS
$BODY$
DECLARE 
	xaccion integer;
	xnewaccion text;
    BEGIN		
		--SELECCIONA accion
		select codigoper into xaccion
		from personal
		where codper=$2;
		
		select newcodigoper into xnewaccion 
		from personal
		where codper=$2;
		insert into transferencias(fecha,codper_padre,codper_hijo,login,obser,accion,interes)
			values($1,$2,$3,$4,$5,xnewaccion,$6);
		
		--INHABILITA A SOCIO ANTIGUO = transfer=si tranfirio su cuenta
		update personal
		set activo=0,transfer=1, codper_transfer=$3
		where codper=$2;
		
		--INHABILITA A SOCIO NUEVO = transfer_sw= si es por tranferecia 
		update personal
		set transfer_sw=1, codper_transfer=$2, codigoper=xaccion, newcodigoper=xnewaccion 
		where codper=$3;
		
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
ALTER FUNCTION public.add_transferencia(date, integer, integer, text, text, integer)
  OWNER TO postgres;
  -- Function: public.add_transferencia(date, integer, integer, text, text, integer)

-- DROP FUNCTION public.add_transferencia(date, integer, integer, text, text, integer);

CREATE OR REPLACE FUNCTION public.add_transferencia(
    xfecha date,
    xcodper_ant integer,
    xcodper_nue integer,
    xlogin text,
    xobser text,
    xinterespagar integer)
  RETURNS text AS
$BODY$
DECLARE 
	xaccion integer;
	xnewaccion text;
    BEGIN		
		--SELECCIONA accion
		select codigoper into xaccion
		from personal
		where codper=$2;
		
		select newcodigoper into xnewaccion 
		from personal
		where codper=$2;
		insert into transferencias(fecha,codper_padre,codper_hijo,login,obser,accion,interes)
			values($1,$2,$3,$4,$5,xnewaccion,$6);
		
		--INHABILITA A SOCIO ANTIGUO = transfer=si tranfirio su cuenta
		update personal
		set activo=0,transfer=1, codper_transfer=$3
		where codper=$2;
		
		--INHABILITA A SOCIO NUEVO = transfer_sw= si es por tranferecia 
		update personal
		set transfer_sw=1, codper_transfer=$2, codigoper=xaccion, newcodigoper=xnewaccion 
		where codper=$3;
		
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
ALTER FUNCTION public.add_transferencia(date, integer, integer, text, text, integer)
  OWNER TO postgres;
  
  
  -- Function: public.deltransferencia(integer, integer, integer, text)

-- DROP FUNCTION public.deltransferencia(integer, integer, integer, text);

CREATE OR REPLACE FUNCTION public.deltransferencia(
    xcodtra integer,
    xcodper_ant integer,
    xcodper_nue integer,
    xlogin text)
  RETURNS text AS
$BODY$
	DECLARE
		xsaldo int;
		
	BEGIN
		--SELECCIONA si tiene saldo o no
		select sum(b.saldo) into xsaldo
		from estado e, boletas b
		where e.codper=$3 and e.estado=1 and e.codestado=b.codestado and b.estado=1 and
			  b.saldo>0;
		
		if (xsaldo is null) then xsaldo=0;  end if;
		if ((xsaldo = 0)) then
			update transferencias
			set estado=0, logindel=$4
			where codtra=$1;
					
			--INHABILITA A SOCIO ANTIGUO = transfer=si tranfirio su cuenta
			update personal
			set activo=1,transfer=0, codper_transfer=0
			where codper=$2;
			
			--INHABILITA A SOCIO NUEVO = transfer_sw= si es por tranferecia 
			update personal
			set transfer_sw=0, codper_transfer=0, codigoper=0, newcodigoper=0, activo=0
			where codper=$3;
		else 
			xsaldo='xxx';
		end if;
		
        RETURN 0;
		EXCEPTION
			when others then 
			return '1';
    END;
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.deltransferencia(integer, integer, integer, text)
  OWNER TO postgres;
  
  
-- Function: public.addaccion(date, text, integer, double precision, text, integer, integer, text, integer, double precision, double precision, double precision)

-- DROP FUNCTION public.addaccion(date, text, integer, double precision, text, integer, integer, text, integer, double precision, double precision, double precision);

CREATE OR REPLACE FUNCTION public.addaccion(
    xfecha date,
    xnro text,
    xcodper integer,
    xmonto double precision,
    xobser text,
    xmesactiv integer,
    xanioactiv integer,
    xlogin text,
    xnrocuota integer,
    xinteres double precision,
    xcuota double precision,
    xmontotal double precision)
  RETURNS text AS
$BODY$
	DECLARE 
		xaccion int;
		xges text;
		xcodigo text;
		xnroaccion int;
		xsw int;
BEGIN		
		--OBTENCION DE DATOS PARA LA FACTURA
		select accion_id,ges,nroaccion into 
				xaccion,xges,xnroaccion
		from general 
		where codg=1;
		
		xaccion=xaccion + 1;
		xcodigo=to_char(xaccion, '00000')||xges;
		xcodigo=trim(xcodigo);
		
		select count(*) into xsw
		from personal
		where newcodigoper=$2 and codper<>$3;
		
		if (xsw is null) then xsw=0; end if;
		if (xsw > 0) then  xsw='xxx'; end if;
		
		insert into accion(coda,fecha,nro,monto,saldo,obs,mesactiv,anioactiv,codper,login,cantcuota,interes,cuota,montotal) 
			values(xcodigo,$1,$2,$4,$12,$5,0,0,$3,$8,$9,$10,$11,$12);		
		
		update personal
		set newcodigoper=$2
		where codper=$3;
		
		--ACTULIZA TABLA GENERAL
		update general set accion_id=xaccion where codg=1;
		
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
ALTER FUNCTION public.addaccion(date, text, integer, double precision, text, integer, integer, text, integer, double precision, double precision, double precision)
  OWNER TO postgres;
  