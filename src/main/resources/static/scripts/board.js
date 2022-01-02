dragula(Array.from((document.querySelectorAll('.draggable-column-elements'))), {
    moves: (el, source, handle, sibling) => !el.classList.contains('no-draggable-element'),
    revertOnSpill: true
})
    .on('drop', (el, target, source, sibling) => {
        let taskId = el.id.replace('task-', '');
        let status = target.id.replace('column-', '');
        changeTaskStatus(taskId, status);
    });

let url = window.location.origin + "/ws";
console.log("Connecting to:" + url);
let socket = new SockJS(url);
let stompClient = Stomp.over(socket);
let params = new URLSearchParams(window.location.search);
let projectId = params.get("projectId");

stompClient.connect({}, function () {
    stompClient.subscribe('/board/' + projectId, function (payload) {
        let task = JSON.parse(payload.body);
        let additionDateTask = moment(task.additionDateTime).format('DD-MM-yyyy');
        document.getElementById("task-" + task.id).remove();
        document.getElementById("column-" + task.taskStatus).insertAdjacentHTML(
            'beforeend',
            `<div class="card" id="task-${task.id}" style="margin-top: 20px;">
                    <div class="card-body">
                        <h5 class="card-title">${task.name}</h5>
                        <h6 class="card-subtitle mb-2 text-muted">${additionDateTask}</h6>
                        <p class="card-text" style="text-align: justify;">${task.description}</p>
                        <a href="/projects/tasks/edit?projectId=${projectId}&taskId=${task.id}"
                       class="card-link">Zadanie</a>
                    </div>
                </div>`
        );
    });
    console.log("Connected to board for project #" + params.get("projectId"));
}, function (error) {
    console.log(error);
});

function changeTaskStatus(taskId, status) {
    stompClient.send("/app/changeTaskStatus/" + params.get("projectId"),
        {},
        JSON.stringify({
            id: taskId,
            taskStatus: status
        })
    );
}
