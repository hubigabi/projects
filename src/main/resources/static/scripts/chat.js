$(document).ready(function () {
    $(".chat_on").click(function () {
        $(".Layout").toggle();
        $(".chat_on").hide(300);
    });

    $(".chat_close_icon").click(function () {
        $(".Layout").hide();
        $(".chat_on").show(300);
    });

    let url = window.location.origin + "/ws";
    console.log("Connecting to:" + url);
    const socket = new SockJS(url);
    var stompClient = Stomp.over(socket);
    var params = new URLSearchParams(window.location.search);

    stompClient.connect({}, function () {
        // onConnected
        stompClient.subscribe('/message/' + params.get("projectId"), function (payload) {
            var message = JSON.parse(payload.body);
            if (message.project.id != parseInt(params.get("projectId"))) return;

            var sender = `${message.user}`;

            var contents = message.message.replaceAll("\n", "<br />");
            var date = new Date(message.localDateTime).toLocaleString("pl")

            $("#msg_list").append(`<div class="Message"><header><span class="sender">${sender}</span> <span class="datetime">${date}</span></header><p>${contents}</p></div>`);
            let list = $("#msg_parent")
            list.scrollTop = list.scrollHeight;
        });
        console.log("Connected to chat for project #" + params.get("projectId"));
    }, function (error) {
        // onError
        console.log(error);
    });

    function sendMessage() {
        let messageField = $("#chat_msg");
        stompClient.send(
            "/app/createMessage/" + params.get("projectId"),
            {},
            JSON.stringify({
                message: messageField.val(),
                localDateTime: new Date(),
                projectId: parseInt(params.get("projectId"))
            })
        );
        console.log("Message sent")
        messageField.val("");
    }

    $("#chat_send").click(sendMessage);

    var shifted = false;
    $(document).on('keyup keydown', function (e) {
        shifted = e.shiftKey
    });

    $("#chat_msg").keypress(function (e) {
        if (e.which == 13) {
            if (shifted) return true;
            else sendMessage();
            return false;
        }
    });

});