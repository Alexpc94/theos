*************************************************
************************************************
*********************************

CREATE OR REPLACE FUNCTION public.login(
    xlogin text,
    xclave text)
  RETURNS text AS
$BODY$
	DECLARE 
		xcant int;
		xcont int;
		xcontalert int;
		xcumple int;
		xres text;
		xnom text;
		xap text;
		xam text;
		miFila RECORD;
		xroles text;
		xmenus text;
		xprocesos text;
		xpriv text;
		xalertas text;
		xalertas2 text;
    BEGIN
		xcant:=0; xcont:=0; xroles:=''; xmenus:=''; xprocesos:=''; xpriv:='';
		select count(*) into xcant from usuarios where (login=$1)and(clave=$2);
		if (xcant > 0) then
			xcont:=1; --INICIALIZA BANDERA
			--sacando datos del usuario
			select p.nombre,p.ap,p.am into xnom,xap,xam
			from usuarios u, personalsis p
			where u.codper=p.codper and u.login=$1;
			--verificando si usuario no tiene AM.
			if (xap  is null) then
				xap='';
			end if;
			if (xam  is null) then
				xam='';
			end if;
			
			--SACANDO LOS ROLES
			FOR miFila in 
					select r.codr, r.nombre
					from usurol ur, roles r
					where ur.login=$1 and ur.codr=r.codr LOOP
				if (xcont > 1) then
						xroles:=xroles||'@';
				end if;
				xroles:=xroles||miFila.codr||'@'||mifila.nombre;
				xcont:=2;
			END LOOP;
			if (xcont = 1) then
				xroles:='-';
			end if;
			
			xcont:=1; --INICIALIZA BANDERA
			
			--SACANDO LOS MENUS
			FOR miFila in 
					select rm.codr,m.codm, m.nombre
					from usurol ur, roles r, rolmen rm, menus m
					where ur.login=$1 and ur.codr=r.codr and
						  r.codr=rm.codr and rm.codm=m.codm	
					order by 1,2 LOOP
				if (xcont > 1) then
						xmenus:=xmenus||'@';
				end if;
				xmenus:=xmenus||miFila.codr||'@'||miFila.codm||'@'||mifila.nombre;
				xcont:=2;
			END LOOP;
			if (xcont = 1) then
				xmenus:='-';
			end if;
			
			xcont:=1; --INICIALIZA BANDERA
			
			--SACANDO PROCESOS
			FOR miFila in 
				select rm.codr,m.codm,p.codp,p.nombre,p.link,p.help
				from usurol ur, roles r, rolmen rm, menus m, menpro mp, procesos p
				where ur.login=$1 and ur.codr=r.codr and
					  r.codr=rm.codr and rm.codm=m.codm	and
					  m.codm=mp.codm and mp.codp=p.codp
				order by 1,2,3 LOOP
				if (xcont > 1) then
						xprocesos:=xprocesos||'@';
				end if;
				xprocesos:=xprocesos||miFila.codr||'@'||miFila.codm||'@'||miFila.codp||'@'||mifila.nombre||'@'||mifila.link||'@'||mifila.help;
				xcont:=2;
			END LOOP;
			if (xcont = 1) then
				xprocesos:='-';
			end if;
			
			xcont:=1; --INICIALIZA BANDERA	
			--SACANDO PRIVILEGIOS
			FOR miFila in 
				select DISTINCT mv.opcion, mv.codp2, mv.codm2
				from usurol ur, roles r, rolmen rm, menus m, menpro mp, mepriv mv
				where ur.login=$1 and ur.codr=r.codr and
					  r.codr=rm.codr and rm.codm=m.codm	and
					  m.codm=mp.codm and 
					  mp.codp=mv.codp2 and mp.codm=mv.codm2
				order by mv.codm2 LOOP
				if (xcont > 1) then
						xpriv:=xpriv||'@';
				end if;
				xpriv:=xpriv||miFila.codm2||'@'||miFila.codp2||'@'||miFila.opcion;
				xcont:=2;
			END LOOP;
			if (xcont = 1) then
				xpriv:='-';
			end if;
			
			--VERIFICAR PARA LAS ALERTAS  xcontalert
			select count(*) into xcontalert
			from  (
				select  p.codigoper
				from personal p, estado e, estadosoc s
				where p.activo=1 and extract(year from age(p.fnac))>=0 and extract(year from age(p.fnac))<21 and
					  p.codper=e.codper and e.sw=1 and e.codes_real <> 200 and e.codes_real=s.codes
				UNION ALL
				select  p.codigoper
				from personal p, estado e, estadosoc s
				where p.activo=1 and extract(year from age(p.fnac))>=21 and extract(year from age(p.fnac))<25 and
					  p.codper=e.codper and e.sw=1 and e.codes_real <> 400 and e.codes_real=s.codes
				UNION ALL
				select  p.codigoper
				from personal p, estado e, estadosoc s
				where p.activo=1 and extract(year from age(p.fnac))>25 and
					  p.codper=e.codper and e.sw=1 and e.codes_real <> 100 and e.codes_real <> 300 and e.codes_real=s.codes
				UNION ALL
				select  pp.codigoper
				from personal p, personal pp  
				where p.benef=1 and p.benef_estado=1 and p.conyuge=0 and extract(year from age(p.fnac))>=21 and  
					  p.padre=pp.codper and pp.activo=1 
			) as datos;
			if (xcontalert is null) then xcontalert=0; end if;
			if (xcontalert > 0) then 
				xalertas='1';
			else
				xalertas='0';
			end if;	
			
			--VERIFICAR PARA LAS ALERTAS CUMPLEAÑOS xcontalert
			SELECT count(*) into xcumple
			FROM personal p ,estado e 
			WHERE p.codper = e.codper 
			AND p.activo = 1 AND p.benef = 0 AND p.estado = 1 AND e.sw = 1 
			AND DATE_PART('day', NOW()) = DATE_PART('day', p.fnac) 
			AND DATE_PART('month', NOW()) = DATE_PART('month', p.fnac);
			
			if (xcumple is null) then xcumple=0; end if;
			if (xcumple > 0) then 
				xalertas2='1';
			else
				xalertas2='0';
			end if;	
			
			xres:=xnom||' '||xap||' '||xam||'>'||xroles||'>'||xmenus||'>'||xprocesos||'>'||xpriv||'>'||xalertas||'>'||xalertas2;
		else 
			--cuando no existe el usuario
			xres:='0';
		end if;
		
        RETURN xres;
    END;
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.login(text, text)
  OWNER TO postgres;

**************************************************
CREATE OR REPLACE FUNCTION xgen_saldo_pendiente_pago(
    xanioini integer,
    xmesini integer,
    xanio integer,
    xmes integer,
    xlogin text,
	xmesdeuda integer)
  RETURNS text AS
$BODY$
DECLARE 
	xsocios RECORD;
	xboletas RECORD;
	ksw integer;
	ktotal float;
	kanio integer;
	kmes integer;
	kobs text;
	xsalant float;
	xcant integer;
BEGIN
		--INICIALIZA
		delete from xsaldosocios where login=$5;
		
		--ANALIZA BOLETA PARA UN SOCIO
		FOR xsocios in 
			select p.codper, p.nombre,p.ap,p.am, s.nombre as estsoc
			from personal p, estado e, estadosoc s
			where p.benef=0 and p.activo=1 and p.estado=1 and p.codper=e.codper and e.sw=1 and e.codes=s.codes
		LOOP
			--SALDO ANTERIOR
			select sum(b.saldo) into xsalant
			from estado e, boletas b
			where e.codper=xsocios.codper and e.codestado=b.codestado and b.estado=1 and b.saldo>0 and 
				  ((b.anio * 12)+b.mes)>=(($1 * 12)+$2) and ((b.anio * 12)+b.mes)<=(($3 * 12)+$4);
			if (xsalant is null) then xsalant=0; end if; 
			
			ksw=0;
			ktotal=0;
			kanio=0;
			kmes=0;
			kobs='';
			xcant=0;
			if (xsalant > 0) then
						insert into xsaldosocios(login,codper,estsocio,saldoant,saldo,total,obs)
						values($5,xsocios.codper,xsocios.estsoc,xsalant,0,0,'');
						
						--LISTA BOLETAS PARA SOMETERLO MES A MES
						FOR xboletas in 
							select e.codper,b.mes,b.anio,b.monto,b.saldo
							from estado e, boletas b
							where e.codper=xsocios.codper and e.codestado=b.codestado and b.estado=1 and b.saldo>0 and 
								 ((b.anio * 12)+b.mes)>=(($1 * 12)+$2) and ((b.anio * 12)+b.mes)<=(($3 * 12)+$4)
							order by b.anio,b.mes
						LOOP
							if (ksw=0) then
								kanio=xboletas.anio;
								ktotal=xboletas.saldo;
								kmes=xboletas.mes;
								ksw=1;
								kobs=cast(kmes as varchar);
								xcant=xcant + 1;
							else 
								kmes=kmes + 1;
								if ((kanio=xboletas.anio)and(kmes=xboletas.mes))	then
									ktotal=ktotal + xboletas.saldo;	
									kobs=kobs ||','|| cast(xboletas.mes as varchar);										
									xcant=xcant + 1;
								else
									kobs=kobs ||'..'|| cast(kanio as varchar)||'=('||cast(ktotal as varchar)||')';
									--INICIALIZA
									kanio=xboletas.anio;
									ktotal=xboletas.saldo;
									kmes=xboletas.mes;
									kobs=kobs||'..'||cast(kmes as varchar);
									xcant=xcant + 1;
								end if;
							end if;							
						END LOOP;  --loope FOR
				if (ksw=1) then 	
					if ((xcant - xmesdeuda) > 0) then
						kobs=kobs ||'..'|| cast(kanio as varchar)||'=('||cast(ktotal as varchar)||')';
						update xsaldosocios 
						set saldoant=xsalant, obs=kobs, total=xcant
						where codper=xsocios.codper and login=$5;
					end if;
				end if;
			end if;	
		END LOOP;  --loop SOCIO
				
        RETURN  '0';
		EXCEPTION
			when others then 
			return '1';
    END;
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.xgen_saldo_deudor(integer, integer, integer, integer, text)
  OWNER TO postgres;
***************************************************
***************************************************
**************************************************