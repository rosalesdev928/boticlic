/**
 * BotiClic — Asistente Virtual IA (Sin API key)
 * Archivo: src/main/resources/static/boticlic-asistente.js
 * Agregar en index.html antes de </body>:
 * <script src="/boticlic-asistente.js"></script>
 */

(function () {
    'use strict';

    // ══════════════════════════════════════════════
    //  BASE DE CONOCIMIENTO — Preguntas y Respuestas
    // ══════════════════════════════════════════════
    const BASE_CONOCIMIENTO = [
        {
            palabras: ['hola', 'buenas', 'buenos', 'hey', 'saludos', 'buen dia', 'buenas tardes', 'buenas noches'],
            respuesta: '¡Hola! 👋 Soy **Boti**, tu asistente de BotiClic. Estoy aquí para ayudarte con información sobre medicamentos, pedidos y más. ¿En qué puedo ayudarte?'
        },
        {
            palabras: ['gracias', 'muchas gracias', 'perfecto', 'excelente', 'genial', 'ok gracias'],
            respuesta: '¡Con gusto! 😊 Si tienes más preguntas, aquí estoy. ¡Que te mejores pronto!'
        },
        {
            palabras: ['adios', 'chao', 'hasta luego', 'nos vemos', 'bye'],
            respuesta: '¡Hasta pronto! 👋 Recuerda que puedes volver cuando necesites. ¡Cuídate mucho!'
        },

        // DELIVERY
        {
            palabras: ['delivery', 'entrega', 'despacho', 'demora', 'cuanto tarda', 'tiempo entrega', 'cuando llega', 'envio', 'envío'],
            respuesta: '🚚 El delivery de BotiClic llega en **menos de 24 horas** dentro de Lima.\n\n• Pedidos antes de las 6pm: entrega el mismo día\n• Pedidos después de las 6pm: entrega al día siguiente\n• También puedes **recoger en tienda** sin costo adicional'
        },
        {
            palabras: ['costo delivery', 'precio delivery', 'cobran delivery', 'gratis envio', 'envio gratis', 'cuanto cuesta envio'],
            respuesta: '💰 El costo de delivery varía según la zona:\n\n• **Envío gratis** en pedidos mayores a S/ 50\n• Usa el código **BOTICLICIO** para 10% de descuento\n• Recojo en tienda: siempre gratis'
        },
        {
            palabras: ['zona', 'distrito', 'donde entregan', 'llegan', 'cobertura', 'lima'],
            respuesta: '📍 Realizamos delivery en **toda Lima Metropolitana**.\n\nSi tienes dudas sobre tu zona específica, puedes consultarlo al momento de hacer tu pedido ingresando tu dirección.'
        },

        // HORARIOS
        {
            palabras: ['horario', 'hora', 'abierto', 'atienden', 'cuando abren', 'cuando cierran', 'disponible'],
            respuesta: '🕐 Nuestros horarios de atención son:\n\n• **Lunes a Sábado:** 8:00 am — 10:00 pm\n• **Domingos y feriados:** 9:00 am — 6:00 pm\n\nEl servicio online está disponible las 24 horas para hacer pedidos.'
        },

        // PAGOS
        {
            palabras: ['pago', 'pagar', 'yape', 'plin', 'tarjeta', 'efectivo', 'transferencia', 'metodo pago', 'forma de pago'],
            respuesta: '💳 Aceptamos los siguientes métodos de pago:\n\n• 💵 **Efectivo** — al momento de la entrega\n• 📱 **Yape** — transferencia inmediata\n• 📲 **Plin** — transferencia inmediata\n• 💳 **Tarjeta** — crédito o débito\n• 🏦 **Transferencia bancaria**'
        },

        // RECETAS
        {
            palabras: ['receta', 'receta medica', 'necesito receta', 'medicamento receta', 'controlado', 'con receta'],
            respuesta: '📋 Para medicamentos que requieren receta médica:\n\n1. Haz tu pedido normalmente\n2. Al finalizar, **sube una foto de tu receta**\n3. Nuestro farmacéutico la validará\n4. Una vez aprobada, procesamos tu pedido\n\n⚠️ Sin receta aprobada, no podemos despachar medicamentos controlados.'
        },
        {
            palabras: ['sin receta', 'venta libre', 'no necesito receta', 'puedo comprar sin'],
            respuesta: '✅ Los siguientes productos son de **venta libre** (sin receta):\n\n• Analgésicos básicos (paracetamol, ibuprofeno)\n• Vitaminas y suplementos\n• Productos de cuidado personal\n• Antihistamínicos comunes\n• Digestivos\n\nPuedes comprarlos directamente sin receta médica.'
        },

        // PRODUCTOS POPULARES
        {
            palabras: ['paracetamol', 'acetaminofen', 'fiebre', 'temperatura', 'dolor cabeza', 'cefalea'],
            respuesta: '💊 El **Paracetamol 500mg** es uno de nuestros más vendidos:\n\n• Para fiebre y dolor leve a moderado\n• No requiere receta médica\n• Dosis adultos: 1-2 tabletas cada 8 horas\n• ⚠️ No exceder 4g por día\n\nPuedes buscarlo en nuestra tienda o agregarlo desde aquí 👇',
            buscarProducto: 'paracetamol'
        },
        {
            palabras: ['ibuprofeno', 'antiinflamatorio', 'dolor muscular', 'inflamacion', 'inflamación'],
            respuesta: '💊 El **Ibuprofeno 400mg** es ideal para inflamación y dolor:\n\n• Antiinflamatorio, analgésico y antipirético\n• No requiere receta\n• Tomar con alimentos para evitar malestar estomacal\n• Dosis: 1 tableta cada 8 horas\n\n⚠️ Consulta a tu médico si tienes problemas gástricos.',
            buscarProducto: 'ibuprofeno'
        },
        {
            palabras: ['vitamina c', 'vitaminas', 'suplemento', 'defensas', 'inmunidad', 'sistema inmune'],
            respuesta: '🍊 Tenemos una excelente línea de **vitaminas y suplementos**:\n\n• Vitamina C 1000mg — refuerza defensas\n• Vitamina D3 — huesos y sistema inmune\n• Complejo B — energía y nervios\n• Zinc — sistema inmunológico\n\nNo requieren receta médica.',
            buscarProducto: 'vitamina'
        },
        {
            palabras: ['antibiotico', 'antibiótico', 'amoxicilina', 'infeccion', 'infección', 'bacteria'],
            respuesta: '🧬 Los **antibióticos requieren receta médica** obligatoriamente.\n\nEl uso sin prescripción puede:\n• Generar resistencia bacteriana\n• Causar efectos secundarios graves\n• No tratar correctamente la infección\n\n🩺 Por favor consulta a tu médico primero. Una vez con receta, con gusto te atendemos.'
        },
        {
            palabras: ['alcohol', 'alcohol gel', 'desinfectante', 'antiseptico', 'antiséptico'],
            respuesta: '🧴 Tenemos productos de **desinfección y cuidado**:\n\n• Alcohol 70% — desinfección general\n• Alcohol en gel — uso sin agua\n• Agua oxigenada — limpieza de heridas\n\nTodos de venta libre, sin receta.',
            buscarProducto: 'alcohol'
        },
        {
            palabras: ['loratadina', 'alergias', 'alergia', 'rinitis', 'estornudo', 'antihistaminico', 'antihistamínico'],
            respuesta: '🤧 Para **alergias** tenemos:\n\n• Loratadina 10mg — no causa sueño\n• Cetirizina — mayor potencia\n• Desloratadina — acción prolongada\n\nLa Loratadina es la más recomendada para el día a día. Venta libre.',
            buscarProducto: 'loratadina'
        },
        {
            palabras: ['omeprazol', 'gastritis', 'acidez', 'reflujo', 'estomago', 'estómago', 'digestivo'],
            respuesta: '🟡 Para problemas **gástricos** tenemos:\n\n• Omeprazol 20mg — protector gástrico\n• Ranitidina — reduce el ácido\n• Bicarbonato — alivio rápido de acidez\n\n⚠️ Si los síntomas persisten más de 2 semanas, consulta a tu médico.',
            buscarProducto: 'omeprazol'
        },

        // PEDIDOS
        {
            palabras: [
                'como comprar', 'como hago', 'como hago un pedido', 'como hacer un pedido',
                'como pido', 'como realizo', 'como realizo un pedido', 'como realizo mi pedido',
                'como se compra', 'como se hace', 'como se hace un pedido', 'como se pide',
                'cómo comprar', 'cómo hago', 'cómo hago un pedido', 'cómo hacer un pedido',
                'cómo pido', 'cómo realizo', 'cómo realizo un pedido',
                'cómo se compra', 'cómo se hace', 'cómo se hace un pedido', 'cómo se pide',
                'como hacer pedido', 'cómo pedir', 'proceso compra', 'pasos compra',
                'quiero comprar', 'quiero pedir', 'quiero hacer un pedido', 'quiero realizar',
                'hacer pedido', 'hacer un pedido', 'hago un pedido', 'hago pedido',
                'realizar pedido', 'realizar un pedido', 'pedido nuevo', 'nuevo pedido',
                'comprar medicamento', 'comprar medicina', 'pedir medicamento',
                'pasos para comprar', 'pasos para pedir', 'instrucciones compra',
                'no se como comprar', 'no se como pedir', 'ayuda para comprar',
                'primera vez', 'primera compra', 'tutorial compra'
            ],
            respuesta: '🛒 Comprar en BotiClic es muy fácil:\n\n1. **Busca** el medicamento en la tienda\n2. **Agrega** al carrito\n3. Haz clic en **"Carrito"** arriba a la derecha\n4. Elige **delivery o recojo**\n5. Ingresa tu dirección y datos\n6. **Paga** con tu método preferido\n7. ¡Listo! Recibes confirmación por correo'
        },
        {
            palabras: ['estado pedido', 'mi pedido', 'donde esta mi pedido', 'rastrear', 'seguimiento', 'mis pedidos'],
            respuesta: '📦 Para ver el estado de tu pedido:\n\n1. Inicia sesión en tu cuenta\n2. Haz clic en tu nombre (arriba a la derecha)\n3. Ve a **"Mis Pedidos"**\n\nEstados posibles:\n• ⏳ **Pendiente** — recibido, en revisión\n• ✅ **Confirmado** — aprobado\n• 🚚 **En camino** — con el repartidor\n• 📬 **Entregado** — recibido'
        },
        {
            palabras: ['cancelar pedido', 'cancelar', 'anular', 'devolucion', 'devolución', 'reembolso'],
            respuesta: '↩️ Para **cancelar o devolver** un pedido:\n\n• Pedidos **pendientes**: puedes cancelar desde "Mis Pedidos"\n• Pedidos **en camino**: llámanos para coordinar\n• **Devoluciones**: aceptamos devoluciones dentro de 24h si el producto está en buen estado\n\nEscríbenos a nuestro WhatsApp para asistencia rápida.'
        },

        // CUENTA
        {
            palabras: ['crear cuenta', 'registrarse', 'registro', 'como me registro', 'cuenta nueva'],
            respuesta: '👤 Crear tu cuenta es gratis y rápido:\n\n1. Haz clic en **"Registrarse"** arriba\n2. Completa tus datos (nombre, email, DNI)\n3. Crea una contraseña\n4. ¡Listo! Ya puedes comprar\n\nCon tu cuenta puedes ver tus pedidos y guardar tu dirección.'
        },
        {
            palabras: ['olvide contraseña', 'olvidé contraseña', 'no puedo entrar', 'recuperar cuenta', 'contraseña'],
            respuesta: '🔑 Si olvidaste tu contraseña:\n\n1. Ve a **"Iniciar Sesión"**\n2. Haz clic en **"¿Olvidaste tu contraseña?"**\n3. Ingresa tu email\n4. Recibirás instrucciones\n\nSi tienes problemas, contáctanos por WhatsApp.'
        },

        // CONTACTO
        {
            palabras: ['contacto', 'telefono', 'teléfono', 'whatsapp', 'llamar', 'escribir', 'comunicar', 'atencion cliente', 'como los contacto', 'numero', 'número'],
            respuesta: '📞 Puedes contactarnos por estos canales:\n\n• 💬 **WhatsApp:** +51 933 406 222\n• 📧 **Email:** soporte@boticlic.pe\n• 📱 **Instagram:** @BotiClic\n• 🌐 **Web:** www.boticlic.pe\n\nHorario de atención:\nLunes a Sábado 8am - 10pm\nDomingos 9am - 6pm\n\nTe respondemos en menos de 15 minutos 🚀'
        },

        // SOBRE BOTICLIC
        {
            palabras: ['que es boticlic', 'quienes son', 'sobre ustedes', 'farmacia', 'botica', 'confiable', 'seguro'],
            respuesta: '💊 **BotiClic** es tu farmacia online de confianza en Lima, Perú.\n\n✅ Productos originales y certificados\n✅ Farmacéuticos certificados\n✅ Delivery rápido y seguro\n✅ Precios competitivos\n✅ Validación de recetas médicas\n\nSomos como Inkafarma, pero con atención más personalizada 😊'
        },
        {
            palabras: ['descuento', 'oferta', 'promocion', 'promoción', 'cupon', 'cupón', 'codigo', 'código'],
            respuesta: '🎁 ¡Tenemos promociones activas!\n\n• Código **BOTICLICIO** → 10% de descuento\n• **Envío gratis** en pedidos mayores a S/ 50\n• Ofertas especiales en vitaminas y suplementos\n\nRevisa nuestro banner superior para ver las promociones del día 👆'
        },

        // EMERGENCIAS
        {
            palabras: ['emergencia', 'urgente', 'urgencia', 'grave', 'hospital', 'samu', '106', 'ambulancia'],
            respuesta: '🚨 Si es una **emergencia médica**:\n\n• Llama al **106** (SAMU - Emergencias)\n• Llama al **117** (SIS)\n• Ve a la **emergencia** del hospital más cercano\n\nNo esperes — la salud primero. BotiClic puede ayudarte después con medicamentos de recuperación.'
        },

        // NO ENTENDIDO (default)
    ];

    // ══════════════════════════════════════════════
    //  ESTILOS
    // ══════════════════════════════════════════════
    const estilos = `
    #bc-btn {
      position: fixed; bottom: 24px; right: 24px;
      width: 60px; height: 60px;
      background: linear-gradient(135deg, #1b5e20, #43a047);
      border-radius: 50%; border: none; cursor: pointer;
      box-shadow: 0 4px 20px rgba(46,125,50,0.45);
      z-index: 9998; font-size: 26px;
      display: flex; align-items: center; justify-content: center;
      transition: transform .2s, box-shadow .2s;
    }
    #bc-btn:hover { transform: scale(1.1); box-shadow: 0 6px 28px rgba(46,125,50,0.6); }
    #bc-badge {
      position: absolute; top: -3px; right: -3px;
      width: 20px; height: 20px; background: #e53935;
      border-radius: 50%; border: 2px solid white;
      font-size: 10px; color: white; font-weight: 700;
      display: none; align-items: center; justify-content: center;
      font-family: 'DM Sans', sans-serif;
    }
    #bc-ventana {
      position: fixed; bottom: 96px; right: 24px;
      width: 370px; max-height: 570px;
      background: white; border-radius: 18px;
      box-shadow: 0 12px 50px rgba(0,0,0,0.18);
      z-index: 9999; display: flex; flex-direction: column;
      overflow: hidden;
      transform: scale(0.88) translateY(16px);
      opacity: 0; pointer-events: none;
      transition: all .28s cubic-bezier(0.34,1.56,0.64,1);
    }
    #bc-ventana.open {
      transform: scale(1) translateY(0);
      opacity: 1; pointer-events: all;
    }
    .bc-head {
      background: linear-gradient(135deg, #1b5e20, #2e7d32);
      padding: 14px 16px;
      display: flex; align-items: center; gap: 10px;
      flex-shrink: 0;
    }
    .bc-head-av {
      width: 40px; height: 40px; border-radius: 50%;
      background: rgba(255,255,255,0.18);
      display: flex; align-items: center; justify-content: center;
      font-size: 20px; flex-shrink: 0;
    }
    .bc-head h4 { color: white; font-size: 14px; font-weight: 700; margin: 0; font-family: 'DM Sans', sans-serif; }
    .bc-head p { color: rgba(255,255,255,0.75); font-size: 11px; margin: 2px 0 0; font-family: 'DM Sans', sans-serif; display: flex; align-items: center; gap: 4px; }
    .bc-dot-online { width: 7px; height: 7px; background: #69f0ae; border-radius: 50%; display: inline-block; animation: bcpulse 2s infinite; }
    @keyframes bcpulse { 0%,100%{opacity:1}50%{opacity:.3} }
    .bc-x { margin-left: auto; background: rgba(255,255,255,.15); border: none; color: white; width: 28px; height: 28px; border-radius: 50%; cursor: pointer; font-size: 15px; display: flex; align-items: center; justify-content: center; transition: background .15s; flex-shrink: 0; }
    .bc-x:hover { background: rgba(255,255,255,.28); }
    .bc-chips {
      padding: 8px 12px 6px;
      display: flex; gap: 5px; flex-wrap: wrap;
      border-bottom: 1px solid #f0f0f0;
      background: #fafafa; flex-shrink: 0;
    }
    .bc-chip {
      padding: 4px 10px; background: white;
      border: 1.5px solid #c8e6c9; border-radius: 14px;
      font-size: 11px; font-weight: 600; color: #2e7d32;
      cursor: pointer; font-family: 'DM Sans', sans-serif;
      transition: all .15s; white-space: nowrap;
    }
    .bc-chip:hover { background: #e8f5e9; border-color: #2e7d32; }
    .bc-msgs {
      flex: 1; overflow-y: auto;
      padding: 14px 12px; display: flex;
      flex-direction: column; gap: 8px;
      scroll-behavior: smooth;
    }
    .bc-msgs::-webkit-scrollbar { width: 3px; }
    .bc-msgs::-webkit-scrollbar-thumb { background: #e0e0e0; border-radius: 3px; }
    .bc-msg { display: flex; gap: 7px; max-width: 88%; animation: bcfade .2s ease; }
    @keyframes bcfade { from{opacity:0;transform:translateY(5px)} to{opacity:1;transform:translateY(0)} }
    .bc-msg.u { align-self: flex-end; flex-direction: row-reverse; }
    .bc-av { width: 28px; height: 28px; border-radius: 50%; flex-shrink: 0; display: flex; align-items: center; justify-content: center; font-size: 13px; }
    .bc-msg.b .bc-av { background: #e8f5e9; }
    .bc-msg.u .bc-av { background: #e3f2fd; }
    .bc-burbuja { padding: 9px 12px; border-radius: 13px; font-size: 13px; line-height: 1.5; font-family: 'DM Sans', sans-serif; word-break: break-word; }
    .bc-msg.b .bc-burbuja { background: #f5f5f5; color: #111; border-bottom-left-radius: 3px; }
    .bc-msg.u .bc-burbuja { background: linear-gradient(135deg,#2e7d32,#43a047); color: white; border-bottom-right-radius: 3px; }
    .bc-burbuja strong { font-weight: 700; }
    .bc-typing .bc-burbuja { display: flex; gap: 4px; align-items: center; padding: 12px 14px; }
    .bc-d { width: 7px; height: 7px; background: #aaa; border-radius: 50%; animation: bcbounce 1.2s infinite; }
    .bc-d:nth-child(2){animation-delay:.2s} .bc-d:nth-child(3){animation-delay:.4s}
    @keyframes bcbounce { 0%,60%,100%{transform:translateY(0)} 30%{transform:translateY(-5px)} }
    .bc-prod {
      background: white; border: 1.5px solid #e8f5e9;
      border-radius: 9px; padding: 9px 11px; margin-top: 6px;
      display: flex; align-items: center; gap: 9px;
      cursor: pointer; transition: all .15s;
    }
    .bc-prod:hover { border-color: #2e7d32; background: #f9fbe7; }
    .bc-prod-ic { font-size: 22px; flex-shrink: 0; }
    .bc-prod-nom { font-size: 12px; font-weight: 700; color: #1a2332; }
    .bc-prod-pre { font-size: 13px; font-weight: 700; color: #2e7d32; }
    .bc-prod-stk { font-size: 10px; color: #888; }
    .bc-prod-add { margin-left: auto; background: #2e7d32; color: white; border: none; border-radius: 6px; padding: 4px 9px; font-size: 11px; font-weight: 600; cursor: pointer; font-family: 'DM Sans', sans-serif; flex-shrink: 0; transition: background .15s; }
    .bc-prod-add:hover { background: #1b5e20; }
    .bc-input-wrap { padding: 10px 12px; border-top: 1px solid #f0f0f0; display: flex; gap: 7px; align-items: flex-end; flex-shrink: 0; }
    #bc-inp { flex: 1; padding: 9px 13px; border: 1.5px solid #e0e0e0; border-radius: 20px; font-size: 13px; font-family: 'DM Sans', sans-serif; outline: none; resize: none; max-height: 90px; overflow-y: auto; line-height: 1.4; transition: border-color .15s; }
    #bc-inp:focus { border-color: #2e7d32; }
    #bc-send { width: 38px; height: 38px; background: #2e7d32; border: none; border-radius: 50%; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: background .15s, transform .15s; flex-shrink: 0; }
    #bc-send:hover { background: #1b5e20; transform: scale(1.05); }
    #bc-send:disabled { background: #ccc; cursor: not-allowed; transform: none; }
    #bc-send svg { width: 16px; height: 16px; fill: white; }
    @media(max-width:480px) {
      #bc-ventana { width: calc(100vw - 20px); right: 10px; }
      #bc-btn { right: 14px; bottom: 14px; }
    }
  `;

    const styleEl = document.createElement('style');
    styleEl.textContent = estilos;
    document.head.appendChild(styleEl);

    // ══════════════════════════════════════════════
    //  HTML
    // ══════════════════════════════════════════════
    document.body.insertAdjacentHTML('beforeend', `
    <button id="bc-btn" title="Asistente BotiClic">
      💊<span id="bc-badge">1</span>
    </button>
    <div id="bc-ventana">
      <div class="bc-head">
        <div class="bc-head-av">🤖</div>
        <div>
          <h4>Asistente BotiClic</h4>
          <p><span class="bc-dot-online"></span> En línea — responde al instante</p>
        </div>
        <button class="bc-x" id="bc-x">✕</button>
      </div>
      <div class="bc-chips">
        <span class="bc-chip" onclick="bcQ('¿Cómo hago un pedido?')">🛒 Pedido</span>
        <span class="bc-chip" onclick="bcQ('¿Tienen paracetamol?')">💊 Paracetamol</span>
        <span class="bc-chip" onclick="bcQ('¿Cuánto demora el delivery?')">🚚 Delivery</span>
        <span class="bc-chip" onclick="bcQ('¿Necesito receta médica?')">📋 Receta</span>
        <span class="bc-chip" onclick="bcQ('¿Cuáles son sus horarios?')">🕐 Horarios</span>
        <span class="bc-chip" onclick="bcQ('¿Qué métodos de pago aceptan?')">💳 Pagos</span>
        <span class="bc-chip" onclick="bcQ('¿Tienen descuentos o promociones?')">🎁 Descuentos</span>
        <span class="bc-chip" onclick="bcQ('¿Cómo los contacto por WhatsApp?')">💬 WhatsApp</span>
        <span class="bc-chip" onclick="bcQ('¿Cómo veo el estado de mi pedido?')">📦 Mis pedidos</span>
        <span class="bc-chip" onclick="bcQ('Es una emergencia médica')">🚨 Emergencia</span>
      </div>
      <div class="bc-msgs" id="bc-msgs"></div>
      <div class="bc-input-wrap">
        <textarea id="bc-inp" placeholder="Escribe tu consulta..." rows="1"></textarea>
        <button id="bc-send">
          <svg viewBox="0 0 24 24"><path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/></svg>
        </button>
      </div>
    </div>
  `);

    // ══════════════════════════════════════════════
    //  LÓGICA
    // ══════════════════════════════════════════════
    let abierto = false;
    let productos = [];

    const btnEl   = document.getElementById('bc-btn');
    const ventEl  = document.getElementById('bc-ventana');
    const cerrarEl= document.getElementById('bc-x');
    const msgsEl  = document.getElementById('bc-msgs');
    const inpEl   = document.getElementById('bc-inp');
    const sendEl  = document.getElementById('bc-send');
    const badgeEl = document.getElementById('bc-badge');

    // Cargar productos de la API
    fetch('http://localhost:8081/api/productos')
        .then(r => r.json())
        .then(d => { productos = d; })
        .catch(() => {});

    // Mostrar badge después de 3 segundos
    // Burbuja "¿En qué puedo ayudarte?"
    const burbuja = document.createElement('div');
    burbuja.id = 'bc-burbuja-msg';
    burbuja.style.cssText = `
    position:fixed; bottom:96px; right:24px;
    background:white; border-radius:14px 14px 4px 14px;
    box-shadow:0 4px 20px rgba(0,0,0,.18);
    padding:11px 16px; font-size:13px; font-weight:600;
    color:#1a2332; z-index:9997; cursor:pointer;
    display:flex; align-items:center; gap:8px;
    animation:bcfadeIn .4s ease; max-width:230px;
    border:1.5px solid #e8f5e9; font-family:'DM Sans',sans-serif;
  `;
    burbuja.innerHTML = `
    <span style="font-size:18px">💊</span>
    <div>
      <div style="font-size:12px;color:#888;font-weight:500">Asistente BotiClic</div>
      <div>¿En qué puedo ayudarte?</div>
    </div>
    <button onclick="event.stopPropagation();document.getElementById('bc-burbuja-msg').remove()" 
      style="background:none;border:none;color:#bbb;font-size:16px;cursor:pointer;padding:0;margin-left:4px;line-height:1">×</button>
  `;
    burbuja.onclick = () => {
        burbuja.remove();
        btnEl.click();
    };
    setTimeout(() => {
        if (!abierto) {
            document.body.appendChild(burbuja);
            badgeEl.style.display = 'flex';
            // Auto-ocultar después de 8 segundos
            setTimeout(() => { if (burbuja.parentNode) burbuja.remove(); }, 8000);
        }
    }, 3000);

    function toggle() {
        abierto = !abierto;
        ventEl.classList.toggle('open', abierto);
        btnEl.innerHTML = abierto
            ? '✕<span id="bc-badge" style="display:none">1</span>'
            : '💊<span id="bc-badge" style="display:none">1</span>';
        if (abierto) {
            if (!msgsEl.children.length) bienvenida();
            inpEl.focus();
        }
    }

    function bienvenida() {
        setTimeout(() => {
            addMsg('¡Hola! 👋 Soy **Boti**, tu asistente farmacéutico de BotiClic.\n\n¿En qué puedo ayudarte hoy? Puedes preguntarme sobre medicamentos, pedidos, delivery, horarios y más.', 'b');
        }, 300);
    }

    function addMsg(texto, tipo, prods) {
        const div = document.createElement('div');
        div.className = `bc-msg ${tipo}`;

        const av = document.createElement('div');
        av.className = 'bc-av';
        av.textContent = tipo === 'b' ? '🤖' : '👤';

        const bur = document.createElement('div');
        bur.className = 'bc-burbuja';

        let html = texto
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/\n/g, '<br>');

        // Mostrar productos relacionados
        if (prods && prods.length) {
            prods.slice(0, 3).forEach(p => {
                const emoji = getEmoji(p.categoria);
                html += `
          <div class="bc-prod">
            <div class="bc-prod-ic">${emoji}</div>
            <div style="flex:1;min-width:0">
              <div class="bc-prod-nom">${p.nombre}</div>
              <div class="bc-prod-pre">S/ ${parseFloat(p.precio).toFixed(2)}</div>
              <div class="bc-prod-stk">${p.stock > 0 ? `✅ ${p.stock} en stock` : '❌ Sin stock'}</div>
            </div>
            ${p.stock > 0 ? `<button class="bc-prod-add" onclick="bcAdd(${p.id})">+ Agregar</button>` : ''}
          </div>`;
            });
        }

        bur.innerHTML = html;
        div.appendChild(av);
        div.appendChild(bur);
        msgsEl.appendChild(div);
        msgsEl.scrollTop = msgsEl.scrollHeight;
    }

    function showTyping() {
        const d = document.createElement('div');
        d.className = 'bc-msg b bc-typing';
        d.id = 'bc-typing';
        d.innerHTML = `<div class="bc-av">🤖</div><div class="bc-burbuja"><div class="bc-d"></div><div class="bc-d"></div><div class="bc-d"></div></div>`;
        msgsEl.appendChild(d);
        msgsEl.scrollTop = msgsEl.scrollHeight;
    }

    function hideTyping() {
        const t = document.getElementById('bc-typing');
        if (t) t.remove();
    }

    function getEmoji(cat) {
        return {'Analgésico':'💊','Antibiótico':'🧬','Antiinflamatorio':'🔴','Digestivo':'🟡','Vitaminas':'🍊','Suplemento':'💪','Antihistamínico':'🤧','Cuidado Personal':'🧴'}[cat] || '💊';
    }

    function buscarRespuesta(msg) {
        const texto = msg.toLowerCase()
            .normalize('NFD').replace(/[\u0300-\u036f]/g, '')
            .replace(/[¿?¡!]/g, '');

        // Buscar en base de conocimiento
        let mejorMatch = null;
        let mejorPuntaje = 0;

        for (const item of BASE_CONOCIMIENTO) {
            let puntaje = 0;
            for (const pal of item.palabras) {
                if (texto.includes(pal)) puntaje += pal.split(' ').length;
            }
            if (puntaje > mejorPuntaje) {
                mejorPuntaje = puntaje;
                mejorMatch = item;
            }
        }

        if (mejorMatch && mejorPuntaje > 0) {
            // Buscar productos relacionados si el item lo pide
            let prodsRel = [];
            if (mejorMatch.buscarProducto && productos.length) {
                const termino = mejorMatch.buscarProducto.toLowerCase();
                prodsRel = productos.filter(p =>
                    p.nombre.toLowerCase().includes(termino) ||
                    (p.categoria || '').toLowerCase().includes(termino)
                ).slice(0, 3);
            }
            return { texto: mejorMatch.respuesta, prods: prodsRel };
        }

        // Buscar producto por nombre directamente
        if (productos.length) {
            const prods = productos.filter(p =>
                texto.includes(p.nombre.toLowerCase().split(' ')[0])
            ).slice(0, 3);

            if (prods.length) {
                return {
                    texto: `Encontré estos productos que podrían ayudarte 👇`,
                    prods
                };
            }
        }

        // Respuesta default
        const defaults = [
            '🤔 No entendí bien tu consulta. ¿Puedes ser más específico? Puedes preguntarme sobre:\n\n• 💊 Medicamentos y precios\n• 🚚 Delivery y horarios\n• 🛒 Cómo hacer un pedido\n• 📋 Recetas médicas\n• 💳 Métodos de pago',
            '😊 No tengo información sobre eso. Puedes contactarnos por WhatsApp para una respuesta más detallada. ¿Te puedo ayudar con algo más?',
            '💊 Esa consulta está fuera de mi alcance. Para atención personalizada, escríbenos por WhatsApp. ¿Tienes preguntas sobre medicamentos o pedidos?'
        ];
        return { texto: defaults[Math.floor(Math.random() * defaults.length)], prods: [] };
    }

    async function responder(msg) {
        addMsg(msg, 'u');
        sendEl.disabled = true;
        inpEl.value = '';
        inpEl.style.height = 'auto';

        showTyping();
        // Simular tiempo de respuesta natural
        await new Promise(r => setTimeout(r, 600 + Math.random() * 500));
        hideTyping();

        const { texto, prods } = buscarRespuesta(msg);
        addMsg(texto, 'b', prods);
        sendEl.disabled = false;
        inpEl.focus();
    }

    // Función global para chips
    window.bcQ = function(msg) {
        // Si es WhatsApp, abrir chat directo
        if (msg.toLowerCase().includes('whatsapp') || msg.toLowerCase().includes('contacto')) {
            window.open('https://wa.me/51933406222?text=Hola%20BotiClic%2C%20necesito%20ayuda%20con%20mi%20pedido%20%F0%9F%92%8A', '_blank');
            addMsg('Te estoy redirigiendo a nuestro **WhatsApp** 💬\n\nSi no abre automáticamente escríbenos al **+51 933 406 222**', 'b');
            return;
        }
        responder(msg);
    };

    window.bcAdd = function(id) {
        if (typeof agregarAlCarrito === 'function') {
            agregarAlCarrito(id);
            addMsg('✅ ¡Producto agregado al carrito! Puedes verlo arriba a la derecha 🛒', 'b');
        } else {
            addMsg('Ve a la tienda y agrégalo desde el botón "Agregar al carrito" 🛒', 'b');
        }
    };

    // Eventos
    btnEl.addEventListener('click', toggle);
    cerrarEl.addEventListener('click', () => {
        abierto = false;
        ventEl.classList.remove('open');
        btnEl.innerHTML = '💊<span id="bc-badge" style="display:none">1</span>';
    });

    sendEl.addEventListener('click', () => {
        if (inpEl.value.trim()) responder(inpEl.value.trim());
    });

    inpEl.addEventListener('keydown', e => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            if (inpEl.value.trim()) responder(inpEl.value.trim());
        }
    });

    inpEl.addEventListener('input', () => {
        inpEl.style.height = 'auto';
        inpEl.style.height = Math.min(inpEl.scrollHeight, 90) + 'px';
    });

    console.log('✅ BotiClic Asistente Virtual listo');
})();