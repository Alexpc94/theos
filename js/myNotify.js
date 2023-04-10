        function menError(xtitulo, xmensaje){
            $.notify({
                title: '<b style="color:red;font-size: 20px;">'+ xtitulo +'</b>',
                message: xmensaje
            },{
                type: 'pastel-danger',
                delay: 5000,
                template: '<div data-notify="container" class="col-xs-12 col-sm-3 alert alert-{0}" role="alert">' +
                    '<span data-notify="title">{1}</span>' +
                    '<span data-notify="message">{2}</span>' +
                    '</div>'
            });
        }


        function menOK(xtitulo, xmensaje){
            $.notify({
                title: '<b style="color:blue;font-size: 20px;">'+ xtitulo +'</b>',
                message: xmensaje
            },{
                type: 'pastel-info',
                delay: 5000,
                template: '<div data-notify="container" class="col-xs-11 col-sm-3 alert alert-{0}" role="alert">' +
                    '<span data-notify="title">{1}</span>' +
                    '<span data-notify="message">{2}</span>' +
                '</div>'
            });
        }    