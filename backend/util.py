import datetime

from django.db.models import Avg

from .models import Substance

import numpy as np


def get_client_ip(request):
    x_forwarded_for = request.META.get('HTTP_X_FORWARDED_FOR')
    if x_forwarded_for:
        ip = x_forwarded_for.split(',')[0]
    else:
        ip = request.META.get('REMOTE_ADDR')
    return ip


def calc_hba1c(value):
    """
    Calculate the HbA1c from the given average blood glucose value.

    This formula is the same one used by Accu-Chek:
    https://www.accu-chek.com/us/glucose-monitoring/a1c-calculator.html#
    """
    if value:
        return ((46.7 + value) / 28.7)
    else:
        return 0


def round_value(value):
    """
    Round the given value to 1 decimal place.

    If the value is 0 or None, then simply return 0.
    """
    if value:
        return round(float(value), 1)
    else:
        return 0


def percent(part, whole):
    """
    Get the percentage of the given values.

    If the the total/whole is 0 or none, then simply return 0.
    """
    if whole:
        return round_value(100 * float(part)/float(whole))
    else:
        return 0


def to_mmol(value):
    """
    Convert a given value in mg/dL to mmol/L rounded to 1 decimal place.
    """
    return round((float(value) / 18.018), 1)


def to_mg(value):
    """
    Convert a given value in mmol/L to mg/dL rounded to nearest integer.
    """
    try:
        return int(round((float(value) * 18.018), 0))
    except ValueError:
        # We're catching ValueError here as some browsers like Firefox won't
        # validate the input for us. We're returning the value entered as this
        # will be passed in to the Django validator which will return the
        # validation error message.
        return value


def get_count_by_category(days):
    now = datetime.datetime.now().date()

    category_count = Substance.objects.by_category(
        (now - datetime.timedelta(days=int(days))), now)

    data = [[c['category__name'], c['count']] for c in category_count]

    return data


def get_avg_by_category(days):
    now = datetime.datetime.now.date()

    averages = Substance.objects.avg_by_category(
        (now - datetime.timedelta(days=int(days))), now)

    data = {'categories': [], 'values': []}
    for avg in averages:
        rounded_value = round_value(avg['avg_value'])
        data['values'].append(rounded_value)
        data['categories'].append(avg['category__name'])

    return data


def get_avg_by_day(category):
    # Get the date of the newest data on the table
    now = Substance.objects.all()[0].record_date
    earliest = Substance.objects.all().reverse()[0].record_date
    averages = Substance.objects.avg_by_day(earliest, now, category)
    data = {'dates': [], 'values': [], 'unit': ''}
    for avg in averages:
        rounded_value = round_value(avg['avg_value'])
        data['values'].append(rounded_value)
        date = avg['record_date']
        date = '{0}/{1}/{2:02}'.format(date.day, date.month, date.year % 100)
        data['dates'].append(date)
    try:
        temp_unit = Substance.objects.filter(category__name=category)[0].unit;
        data['unit'] = temp_unit.name
    except:
        data['unit'] = ''
    return data


def get_stats(category):
    # Get the date of the newest data on the table
    data = {'avg': '','std': '',
            'latest':'', 'latest_date':'','highest':'', 'highest_date':'', 'lowest':'', 'lowest_date':'','unit': ''}
    try:
        all_substances = Substance.objects.all().filter(category__name=category)
        values = np.array([f.value for f in all_substances])
        record_date = np.array([f.record_date for f in all_substances])

        data['avg'] = np.average(values)
        data['std'] = round(np.std(values), 2)
        data['latest'] = values[0]
        data['latest_date'] = '{0}/{1}/{2:02}'.format(record_date[0].day, record_date[0].month, record_date[0].year % 100)
        data['highest'] = np.max(values)
        data['highest_date'] = '{0}/{1}/{2:02}'.format(record_date[np.argmax(values)].day,
                                                       record_date[np.argmax(values)].month,
                                                       record_date[np.argmax(values)].year % 100)
        data['lowest'] = np.min(values)
        data['lowest_date'] = '{0}/{1}/{2:02}'.format(record_date[np.argmin(values)].day,
                                                      record_date[np.argmin(values)].month,
                                                      record_date[np.argmin(values)].year % 100)
        data['unit'] = all_substances[0].unit.name
    except:
        print('query error, data will be blank')
    return data
