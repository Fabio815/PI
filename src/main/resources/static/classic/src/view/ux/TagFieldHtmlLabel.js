Ext.define('ProjSistemaOs.view.ux.TagFieldHtmlLabel', {
        extend: 'Ext.form.field.Tag',
        alias: 'widget.tagfieldhtmllabel',
        requires: [
            'Ext.form.field.Tag'
        ],
        getMultiSelectItemMarkup: function () {
            var a = this,
                b = a._getChildElCls &&
                    a._getChildElCls() ||
                    '';
            a.multiSelectItemTpl ||
            (
                a.labelTpl ||
                (a.labelTpl = '{' + a.displayField + '}'),
                    a.labelTpl = a.lookupTpl('labelTpl'),
                a.tipTpl &&
                (a.tipTpl = a.lookupTpl('tipTpl')),
                    a.multiSelectItemTpl = new Ext.XTemplate(
                        ['<tpl for=".">',
                            '<li data-selectionIndex="{[xindex - 1]}" data-recordId="{internalId}" role="presentation" class="' + a.tagItemCls +
                            b,
                            '<tpl if="this.isSelected(values)">',
                            ' ' + a.tagSelectedCls,
                            '</tpl>',
                            '{%',
                            'values = values.data;',
                            '%}',
                            a.tipTpl ? '" data-qtip="{[this.getTip(values)]}">' : '">',
                            '<div role="presentation" class="' + a.tagItemTextCls + '">{[this.getItemLabel(values)]}</div>',
                            '<div role="presentation" class="' + a.tagItemCloseCls + b + '"></div>',
                            '</li>',
                            '</tpl>',
                            {
                                isSelected: function (e) {
                                    return a.selectionModel.isSelected(e)
                                },
                                getItemLabel: function (e) {
                                    return a.labelTpl.apply(e)
                                },
                                getTip: function (e) {
                                    return Ext.String.htmlEncode(a.tipTpl.apply(e))
                                },
                                strict: !0
                            }
                        ]
                    )
            );
            a.multiSelectItemTpl.isTemplate ||
            (a.multiSelectItemTpl = this.lookupTpl('multiSelectItemTpl'));
            return a.multiSelectItemTpl.apply(a.valueCollection.getRange())
        }
    }
);