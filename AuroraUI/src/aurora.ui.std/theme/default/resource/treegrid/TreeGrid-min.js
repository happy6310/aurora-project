$A.TreeGrid=Ext.extend($A.Grid,{initComponent:function(d){$A.TreeGrid.superclass.initComponent.call(this,d);delete d.marginheight;delete d.marginwidth;var c=(this.selectable&&this.lockColumns.length==1)||this.lockColumns.length==0;var a;if(this.lockColumns.length>0){var b=this.id+"_lb_tree";this.lb.set({id:b}).addClass("item-treegrid");a=this.lockTree=this.createTree(this.createTreeConfig(d,this.lockColumns,b,!c,this));a.body=this.lb;a.treegrid=this}var e=this.id+"_ub_tree";this.ub.set({id:e}).addClass("item-treegrid");var f=this.unlockTree=this.createTree(this.createTreeConfig(d,this.unlockColumns,e,c,this));f.body=this.ub;f.treegrid=this;this.initTreeLisener(a,f)},initTreeLisener:function(a,b){if(a){a.on("render",function(){this.processData();Ext.DomHelper.insertHtml("beforeEnd",this.lb.dom,'<div style="height:17px"></div>')},this);a.on("expand",function(c,d){this.unlockTree.getNodeById(d.id).expand()},this);a.on("collapse",function(c,d){this.unlockTree.getNodeById(d.id).collapse()},this)}b.on("render",this.processData,this);if(a){b.on("expand",function(c,d){this.lockTree.getNodeById(d.id).expand()},this);b.on("collapse",function(c,d){this.lockTree.getNodeById(d.id).collapse()},this)}},initTemplate:function(){$A.TreeGrid.superclass.initTemplate.call(this);this.cbTpl=new Ext.Template('<center style="width:{width}px"><div class="{cellcls}" style="height:13px;padding:0px;" id="'+this.id+'_{name}_{recordid}"></div></center>')},createTemplateData:function(c,a){var b=c.name,d=this.getEditor(c,a),e=c.type?2:(Ext.isEmpty(b&&a.getField(b).get("checkedvalue"))?(this.editorborder&&d!=""&&(d=$A.CmpManager.get(d))?7:5):1);return{width:c.width-e,recordid:a.id,visibility:c.hidden===true?"hidden":"visible",name:b}},createTree:function(a){return new $A.Tree(a)},createTreeConfig:function(b,e,i,a,d){var g=e[0];if((g.type=="rowcheck"||g.type=="rowradio")&&e.length>1){g=e[1]}var f=g?g.width:150;return Ext.apply(b,{sw:20,id:i,showSkeleton:a,width:f,column:g,displayfield:g.name,renderer:g.renderer,initColumns:function(c){if(c.isRoot()&&c.ownerTree.showRoot==false){return}Ext.each(e,function(n){var j=n.name,k=c.record;if(n.type=="rowcheck"||n.type=="rowradio"){new Ext.Template(d.createCell(n,k,null)).insertFirst(c.els.itemNodeTr,{},true)}else{if(j==c.ownerTree.displayfield){return}else{var m=document.createElement("td"),l=n.align;c.els[j+"_td"]=m;if(l){m.style.textAlign=l}m.recordid=k.id;m._type_="text";m.atype="grid-cell";m.dataindex=j;m.appendChild(c.els[j+"_text"]=Ext.DomHelper.insertHtml("afterBegin",m,d.createCell(n,k,true)));c.els.itemNodeTr.appendChild(m);Ext.fly(m).setWidth(n.width-1).addClass("node-text")}}})},createTreeNode:function(c){return new $A.Tree.TreeGridNode(c)},onNodeSelect:function(c){if(c){c.itemNodeTable.style.backgroundColor="#dfeaf5"}},onNodeUnSelect:function(c){if(c){c.itemNodeTable.style.backgroundColor=""}}})},processData:function(a,b){if(!b){return}var d=this,c=[];if(a.showRoot){d.processNode(c,b)}else{Ext.each(b.children,function(e){d.processNode(c,e)})}d.dataset.data=c;d.editing&&d.positionEditor()},onLoad:function(){this.drawFootBar();$A.Masker.unmask(this.wb)},processNode:function(a,b){a.add(b.record);Ext.each(b.children,function(c){this.processNode(a,c)},this)},bind:function(b){if(typeof(b)==="string"){b=$A.CmpManager.get(b);if(!b){return}}var a=this;a.dataset=b;a.processDataSetLiestener("on");if(a.lockTree){a.lockTree.bind(b)}a.unlockTree.bind(b);a.drawFootBar()},setColumnSize:function(b,d){$A.TreeGrid.superclass.setColumnSize.call(this,b,d);var e=this,f=e.findColByName(b),a=f.lock==true?e.lockTree:e.unlockTree;f.width=d;if(b==a.displayfield){a.width=d}a.root&&a.root.setWidth(b,d)},renderLockArea:function(){var b=this,a=0;Ext.each(b.columns,function(d){if(d.lock===true&&d.hidden!==true){a+=d.width}});b.lockWidth=a},focusRow:function(i){var g=this,b=g.dataset.getAt(i),f=g.unlockTree.getNodeById(b.id),e=f?f.els:null;if(!e){return}var c=g.ub,d=c.getScroll().top,a=Ext.fly(e.element).getTop()-c.getTop()+d;r=25,h=c.getHeight(),sh=c.dom.scrollWidth>c.dom.clientWidth?16:0;(function(){if(a<d){c.scrollTo("top",a-1)}else{if(a+r>(d+h-sh)){c.scrollTo("top",a+r-h+sh)}}}).defer(1)},onUpdate:function(i,b,d,g){$A.TreeGrid.superclass.onUpdate.call(this,i,b,d,g);var f=this,j=((f.selectable&&f.lockColumns.length==1)||f.lockColumns.length==0)?f.unlockTree:f.lockTree,e=j.getNodeById(b.id),a=e.getOwnerTree();e.els&&e.doSetWidth(a.displayfield,a.width)},onMouseWheel:function(a){},onAdd:function(){}});$A.Tree.TreeGridNode=Ext.extend($A.Tree.TreeNode,{createNode:function(a){return new $A.Tree.TreeGridNode(a)},createCellEl:function(e){var d=this,b=d.getOwnerTree(),a=b.column,g=a.align,c=d.record,f=d.els[e+"_td"];d.els[e+"_text"]=Ext.DomHelper.insertHtml("afterBegin",f,b.treegrid.createCell(a,c,true));f.dataindex=e;f.atype="grid-cell";f.recordid=c.id;if(g){f.style.textAlign=g}},paintText:function(){},render:function(){$A.Tree.TreeGridNode.superclass.render.call(this);var a=this.getOwnerTree();this.setWidth(a.displayfield,a.width)}});$A.TreeGrid.revision="$Rev: 8326 $";