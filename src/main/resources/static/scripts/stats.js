const labels = stats.days.map(day => dayjs(day).format('DD-MM'));

let datasets = [];
const colors = palette('mpn65', stats.finishedTaskStatuses.length);
stats.finishedTaskStatuses.reverse().forEach((element, index) => {
    const dataset = {
        label: element.taskStatus,
        data: element.finishedTaskStatus,
        borderColor: '#' + colors[index],
        backgroundColor: addAlpha('#' + colors[index], 0.5),
        fill: true,
        tension: 0.1,
    };
    datasets.push(dataset);
});

const data = {
    labels: labels,
    datasets: datasets
};

const config = {
    type: 'line',
    data: data,
    options: {
        plugins: {
            title: {
                display: true,
                text: 'Skumulowany diagram przepływu'
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                ticks: {
                    precision: 0
                }
            }
        }
    },
};

const statsChart = new Chart(
    document.getElementById('statsChart'),
    config
);

function addAlpha(color, opacity) {
    const _opacity = Math.round(Math.min(Math.max(opacity || 1, 0), 1) * 255);
    return color + _opacity.toString(16).toUpperCase();
}